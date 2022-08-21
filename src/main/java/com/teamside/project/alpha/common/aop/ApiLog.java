package com.teamside.project.alpha.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import com.teamside.project.alpha.common.aop.service.LogService;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.util.CryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        StringBuilder desc = new StringBuilder();
        String apiStatus = KeepitConstant.SUCCESS;
        String methodName = "";
        String controllerName = "";
        String mid = CryptUtils.getMid();
        int apiCode = 0;
        try {
            methodName = joinPoint.getSignature().getName();

            String[] packagedMethodName = joinPoint.getTarget().getClass().getName().split("\\.");
            controllerName = packagedMethodName[packagedMethodName.length - 1];

            String params = getRequestParams().replace("\\", "");

            desc.append("[REQUEST] METHOD : ").append(controllerName).append("{").append(methodName).append("}\nDATA : ").append(params);

            Map<String, String> pathVariablesMap = (Map<String, String>)httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            StringBuilder pathVariables = new StringBuilder();
            if (pathVariablesMap.size() >= 1) {
                for (Map.Entry<String, String> entry : pathVariablesMap.entrySet()) {
                    pathVariables.append("\n").append(entry.getKey()).append(" -> ").append(entry.getValue());
                }
                desc.append("\nPATHVARIABLES : ").append(pathVariables);
            }

            result = joinPoint.proceed();

            apiCode = ((ResponseEntity) result).getStatusCodeValue();
            stopWatch.stop();
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            desc.append("\n").append("[RESPONSE] ").append(objectMapper.writeValueAsString(response.getBody()));

        } catch (Exception ex) {
            stopWatch.stop();
            if (ex instanceof CustomException) {
                apiCode = ((CustomException) ex).getApiExceptionCode().getApiCode();
                desc.append("[RESPONSE] ").append(objectMapper.writeValueAsString(((CustomException) ex).getErrorDetail()));
            }
            if (ex instanceof CustomRuntimeException) {
                apiCode = ((CustomRuntimeException) ex).getApiExceptionCode().getApiCode();
                desc.append("[RESPONSE] ").append(objectMapper.writeValueAsString(((CustomRuntimeException) ex).getErrorDetail()));
            }
            if (ex instanceof ConstraintViolationException) {
                apiCode = 999;
                desc.append("[RESPONSE] ").append(((ConstraintViolationException) ex).getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining()))  ;

            }
            apiStatus = KeepitConstant.FAIL;
            throw ex;
        }
        finally {
            desc.append("\n").append("PROCESS_TIME : ").append(stopWatch.getTotalTimeMillis() * 0.001);
            log.info("\n" + desc);
            ApiLogEntity apiLogEntity = new ApiLogEntity(mid, methodName, desc.toString(), apiStatus, (float) (stopWatch.getTotalTimeMillis() * 0.001), apiCode);
            logService.insertLog(apiLogEntity);
        }


        return result;
    }


    private String getRequestParams() throws JsonProcessingException {
        String params = "{}";

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            Map<String, String[]> paramMap = requestAttributes.getRequest().getParameterMap();
            if (!paramMap.isEmpty()) {
                params = " [" + getWrapperParamJson(paramMap) + "]";
            }
        }

        return params;

    }

    private String getWrapperParamJson(Map<String, String[]> wrapperParams) throws JsonProcessingException {
        Map<String, String> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

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
        return mapper.writeValueAsString(params);
    }
}
