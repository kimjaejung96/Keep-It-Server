package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.domain.terms.model.entity.TermsEntity;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.service.AuthService;
import com.teamside.project.alpha.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepo memberRepo;
    private final AuthService authService;

    public MemberServiceImpl(MemberRepo memberRepo, AuthService authService) {
        this.memberRepo = memberRepo;
        this.authService = authService;
    }

    @Override
    @Transactional
    public JwtTokens sigunUp(MemberDto.SignUpDto signUpDto) throws CustomException {
        checkExistName(signUpDto.getMember().getName());
        checkExistPhone(signUpDto.getMember().getPhone());

        MemberEntity member = new MemberEntity(
                UUID.randomUUID().toString(),
                signUpDto.getMember().getName(),
                CryptUtils.encrypt(signUpDto.getMember().getPhone()),
                signUpDto.getMember().getProfileUrl(),
                signUpDto.getMember().getPinProfileUrl(),
                signUpDto.getMember().getFcmToken(),
                SignUpType.PHONE);


        member.changeTerms(new TermsEntity(member,
                signUpDto.getTerms().getTerms(),
                signUpDto.getTerms().getCollect(),
                signUpDto.getTerms().getMarketing(),
                signUpDto.getTerms().getAlarm()));

        JwtTokens jwtTokens = authService.getTokens(member.getMid());

        member.changeRefreshToken(jwtTokens.getRefreshToken());

        memberRepo.save(member);
        return jwtTokens;
    }

    @Override
    public void checkExistsName(String name) throws CustomException {
        checkExistName(name);
    }

    @Override
    @Transactional
    public void logout() {
        Optional<MemberEntity> member = memberRepo.findByMid(CryptUtils.getMid());
        member.ifPresent(MemberEntity::logout);
    }

    private void checkExistName(String name) throws CustomException {
        if (memberRepo.existsByName(name)) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
    }

    private void checkExistPhone(String phone) throws CustomException {
        if (memberRepo.existsByPhone(phone)) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_PHONE);
        }
    }
}
