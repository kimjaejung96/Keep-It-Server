package com.teamside.project.alpha.common.aop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import com.teamside.project.alpha.common.aop.service.LogService;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ApiLog {
    private final LogService logService;
    private final ObjectMapper objectMapper;

    public ApiLog(LogService logService, ObjectMapper objectMapper) {
        this.logService = logService;
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.teamside.project.alpha..controller..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        String desc = "";
        String status = KeepitConstant.SUCCESS;
        String methodName = "";
        String controllerName = "";
        try {
            methodName = joinPoint.getSignature().getName();

            String[] packagedMethodName = joinPoint.getTarget().getClass().getName().split("\\.");
            controllerName = packagedMethodName[packagedMethodName.length - 1];

            String params = getRequestParams();

            log.info("=======> REQUEST : {} / METHOD : {} / PARAMS : {}", joinPoint.getSignature().getDeclaringTypeName(), methodName, params);
            desc += "[REQUEST] METHOD : " + controllerName + "{" + methodName + "} PARAMS : " + params + "\n";

            result = joinPoint.proceed();
            stopWatch.stop();

            ResponseEntity<?> response = (ResponseEntity<?>) result;
            desc += "[RESPONSE] " + objectMapper.writeValueAsString(response.getBody());

            // TODO: 2022/08/08 임시데이터 MID  secret context에서 꺼내와야함.
            log.info("<======= RESPONSE : {} / METHOD : {} / RESULT : {} / PROCESS_TIME =  {}s", joinPoint.getSignature().getDeclaringTypeName(), methodName, objectMapper.writeValueAsString(response.getBody()), stopWatch.getTotalTimeMillis() * 0.001);
        } catch (Exception ex) {
            stopWatch.stop();
            status = KeepitConstant.FAIL;
            throw ex;
        }
        finally {
            ApiLogEntity apiLogEntity = new ApiLogEntity("mId", methodName, desc, status, (float) (stopWatch.getTotalTimeMillis() * 0.001));
            logService.insertLog(apiLogEntity);
        }


        return result;
    }


    private String getRequestParams() throws JsonProcessingException {
        String params = "PARAM IS NULL";

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
