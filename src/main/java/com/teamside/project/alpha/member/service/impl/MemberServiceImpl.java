package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.service.MemberService;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepo memberRepo;

    public MemberServiceImpl(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
    }

    @Override
    public boolean checkId(String name)  {
        return memberRepo.existsByName(name);
    }
}
