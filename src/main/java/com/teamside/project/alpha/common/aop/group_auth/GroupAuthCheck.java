package com.teamside.project.alpha.common.aop.group_auth;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@Aspect
@RequiredArgsConstructor
public class GroupAuthCheck {
    private final GroupRepository groupRepository;

    @Around("@annotation(com.teamside.project.alpha.common.custom_annotaion.GroupAuthCheck)")
    public Object groupAuthCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] params = methodSignature.getParameterNames();

        int idx = Arrays.asList(params).indexOf("groupId");
        if (idx == -1) {
            throw new CustomRuntimeException(ApiExceptionCode.BAD_REQUEST);
        }
        String groupId = args[idx].toString();
        authCheck(groupId);
        return joinPoint.proceed();
    }

    @Transactional
    public void authCheck(String groupId)  {
        if (!groupRepository.groupAuthCheck(groupId)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }
}
