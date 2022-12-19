package com.teamside.project.alpha.common.aop.group_auth;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

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
        String groupId = args[idx].toString();
        authCheck(groupId);
        return null;
    }
    private void authCheck(String groupId) throws CustomException {
        String userMid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.getGroupMemberMappingEntity().stream()
                .filter(member -> member.getMid().equals(userMid))
                .filter(member -> member.getStatus().equals(GroupMemberStatus.JOIN))
                .noneMatch(e -> {throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);});
    }
}
