package com.teamside.project.alpha.member.domain.mypage.service.impl;

import com.teamside.project.alpha.member.domain.mypage.model.dto.MyPageHome;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {
    private final MemberRepo memberRepo;
    @Override
    public MyPageHome getMyPageHome() {
        return memberRepo.getMyPageHome();
    }
}
