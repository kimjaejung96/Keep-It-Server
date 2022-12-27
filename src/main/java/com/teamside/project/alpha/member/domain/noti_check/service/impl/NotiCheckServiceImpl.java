package com.teamside.project.alpha.member.domain.noti_check.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.domain.noti_check.service.NotiCheckService;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.MemberRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotiCheckServiceImpl implements NotiCheckService {
    private final MemberRepo memberRepo;

    public NotiCheckServiceImpl(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean notiCheck() throws CustomException {
        String mid = CryptUtils.getMid();
        LocalDateTime checkTime;

        // 알람시간 + 1s ~ 현재시간
        MemberEntity member = memberRepo.findByMid(mid)
                .orElseThrow(() -> new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND));

        LocalDateTime twoWeeksTime = LocalDateTime.now().minusWeeks(2);
        LocalDateTime lastCheckTime = member.getNotiCheck().getUpdateTime().plusSeconds(1);

        if (lastCheckTime.isAfter(twoWeeksTime)) {
            checkTime = lastCheckTime;
        } else {
            checkTime = twoWeeksTime;
        }

        return memberRepo.notiCheck(mid, checkTime);
    }
}
