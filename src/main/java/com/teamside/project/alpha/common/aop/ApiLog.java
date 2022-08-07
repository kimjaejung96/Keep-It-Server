package com.teamside.project.alpha.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import com.teamside.project.alpha.common.aop.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ApiLog {
    private final LogService logService;

    public ApiLog(LogService logService) {
        this.logService = logService;
    }

    @Around("execution(* com.teamside.project.alpha..controller..*(..))" )
    public void logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startAt = System.currentTimeMillis();
        String params = getRequestParams();
        log.info("=======> REQUEST : {} / METHOD : {} / PARAMS : {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), params);

        ResponseEntity response = (ResponseEntity) joinPoint.proceed();
        long endAt = System.currentTimeMillis();

        String desc = "[REQUEST] METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "-"+ joinPoint.getSignature().getName() + " PARAMS : " + params + "\n"
        + "[RESPONSE] " + response.getBody() + " - " + response.getStatusCode();

        log.info("<======= RESPONSE : {} / METHOD : {} / RESULT : {} / PROCESS_TIME =  {}ms", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), response, endAt - startAt);

        ApiLogEntity apiLogEntity = new ApiLogEntity("mId", "mName", desc, response.getStatusCode().toString(),endAt - startAt);
        logService.insertLog(apiLogEntity);
    }


    private String getRequestParams() throws JsonProcessingException {
        String params = "NULL";

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
