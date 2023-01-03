package com.teamside.project.alpha.common.aop.api_log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.aop.api_log.model.entity.ApiLogEntity;
import com.teamside.project.alpha.common.aop.api_log.service.LogService;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.util.CryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class ApiLog {
    private final LogService logService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest httpServletRequest;

    public ApiLog(LogService logService, ObjectMapper objectMapper, HttpServletRequest httpServletRequest) {
        this.logService = logService;
        this.objectMapper = objectMapper;
        this.httpServletRequest = httpServletRequest;
    }

    @Around("execution(* com.teamside.project.alpha..controller..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("\nRequest Full URI : {}", httpServletRequest.getRemoteHost() + httpServletRequest.getRequestURI());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        StringBuilder desc = new StringBuilder();
        StringBuilder logs = new StringBuilder();
        String apiStatus = KeepitConstant.SUCCESS;
        String methodName = "";
        String controllerName;
        String mid = CryptUtils.getMid();
        int apiCode = 0;
        try {
            methodName = joinPoint.getSignature().getName();

            String[] packagedMethodName = joinPoint.getTarget().getClass().getName().split("\\.");
            controllerName = packagedMethodName[packagedMethodName.length - 1];

            String params = getRequestParams().replace("\\", "").replace("\"{", "{").replace("}\"", "}");

            logs.append("[REQUEST] method : ").append(controllerName).append("{").append(methodName).append("}\nDATA : ").append(params).append("\n");
            desc.append("[REQUEST] ").append("\ndata : ").append(params).append("\n");

            Map<String, String> pathVariablesMap = (Map<String, String>)httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            StringBuilder pathVariables = new StringBuilder();
            if (pathVariablesMap.size() >= 1) {
                for (Map.Entry<String, String> entry : pathVariablesMap.entrySet()) {
                    pathVariables.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
                }
                logs.append("pathVariables : \n").append(pathVariables);
                desc.append("pathVariables : \n").append(pathVariables);
            }

            result = joinPoint.proceed();

            stopWatch.stop();
            responseEntity = (ResponseEntity<?>) result;
            apiCode = responseEntity.getStatusCodeValue();
            desc.append("[RESPONSE]\n").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));
            logs.append("[RESPONSE]\n").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity.getBody()));

        } catch (Exception ex) {
            stopWatch.stop();
            if (ex instanceof CustomException) {
                apiCode = ((CustomException) ex).getApiExceptionCode().getApiCode();
                desc.append("[RESPONSE]\n").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(((CustomException) ex).getErrorDetail()));
                logs.append("[RESPONSE]\n").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(((CustomException) ex).getErrorDetail()));
            }
            if (ex instanceof CustomRuntimeException) {
                apiCode = ((CustomRuntimeException) ex).getApiExceptionCode().getApiCode();
                desc.append("[RESPONSE]\n").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(((CustomRuntimeException) ex).getErrorDetail()));
                logs.append("[RESPONSE]\n").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(((CustomRuntimeException) ex).getErrorDetail()));
            }
            if (ex instanceof ConstraintViolationException) {
                apiCode = 999;
                desc.append("[RESPONSE]\n").append(((ConstraintViolationException) ex).getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining()))  ;
                logs.append("[RESPONSE]\n").append(((ConstraintViolationException) ex).getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining()))  ;

            }
            apiStatus = KeepitConstant.FAIL;
            throw ex;
        }
        finally {
            logs.append("\n").append("PROCESS_TIME : ").append(stopWatch.getTotalTimeMillis() * 0.001);
            log.info("\n" + logs);
            ApiLogEntity apiLogEntity = new ApiLogEntity(mid, methodName, desc.toString(), apiStatus, (float) (stopWatch.getTotalTimeMillis() * 0.001), apiCode);
            apiCheck(apiLogEntity, joinPoint, responseEntity);
        }
        return result;
    }

    private void apiCheck(ApiLogEntity apiLogEntity, ProceedingJoinPoint joinPoint, ResponseEntity<?> responseEntity) {
        String filteredMethodName = joinPoint.getSignature().getName();

        if (filteredMethodName.equals("notiCheck") && responseEntity.getStatusCode().value() == 200) {

        } else CompletableFuture.runAsync(() -> logService.insertLog(apiLogEntity));
    }


    private String getRequestParams() throws JsonProcessingException {
        String params = "{}";

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            Map<String, String[]> paramMap = requestAttributes.getRequest().getParameterMap();
            if (!paramMap.isEmpty()) {
                params = getWrapperParamJson(paramMap);
            }
        }

        return params;

    }

    private String getWrapperParamJson(Map<String, String[]> wrapperParams) throws JsonProcessingException {
        Map<String, String> params = new HashMap<>();

        for (Map.Entry<String, String[]> entry : wrapperParams.entrySet()) {
            String[] values = wrapperParams.get(entry.getKey());
            StringBuilder sumValue = new StringBuilder();
            for (String value : values)
                if ((sumValue.length() != 0))
                    sumValue.append(", ").append(value);
                else
                    sumValue.append(value);
            params.put(entry.getKey(), sumValue.toString());
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params);
    }
}
