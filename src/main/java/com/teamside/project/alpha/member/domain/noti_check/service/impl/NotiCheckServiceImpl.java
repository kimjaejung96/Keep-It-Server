package com.teamside.project.alpha.member.domain.noti_check.service.impl;

import com.teamside.project.alpha.member.domain.noti_check.service.NotiCheckService;
import com.teamside.project.alpha.member.repository.MemberRepo;
import org.springframework.stereotype.Service;

@Service
public class NotiCheckServiceImpl implements NotiCheckService {
    private final MemberRepo memberRepo;

    public NotiCheckServiceImpl(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
    }

    @Override
    public void notiCheck() {
        // 알람시간 + 1 ~ 현재 시간
    }
}
