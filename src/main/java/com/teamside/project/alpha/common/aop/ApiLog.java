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

    public ApiLog(LogService logService) {
        this.logService = logService;
    }

    @Around("execution(* com.teamside.project.alpha..controller..*(..))" )
    public void logging(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String params = getRequestParams();
        log.info("=======> REQUEST : {} / METHOD : {} / PARAMS : {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), params);

        ResponseEntity response = (ResponseEntity) joinPoint.proceed();
        stopWatch.stop();

        ObjectMapper objectMapper = new ObjectMapper();


        String[] packagedMethodName = joinPoint.getTarget().getClass().getName().split("\\.");
        String methodName = packagedMethodName[packagedMethodName.length-1];

        String desc = "[REQUEST] METHOD : " + methodName + "{"+ joinPoint.getSignature().getName() + "} PARAMS : " + params + "\n"
        + "[RESPONSE] " + objectMapper.writeValueAsString(response.getBody()) + " - " + response.getStatusCode();

        log.info("<======= RESPONSE : {} / METHOD : {} / RESULT : {} / PROCESS_TIME =  {}s", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), objectMapper.writeValueAsString(response.getBody()), (float) stopWatch.getTotalTimeMillis()*0.001);

        // TODO: 2022/08/08 임시데이터 MID  secret context에서 꺼내와야함.
        ApiLogEntity apiLogEntity = new ApiLogEntity("mId", joinPoint.getSignature().getName(), desc, response.getStatusCode().toString(), (float) (stopWatch.getTotalTimeMillis()*0.001));
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
