package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.domain.TermsEntity;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.entity.InquiryEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import com.teamside.project.alpha.member.repository.InquiryRepo;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.service.AuthService;
import com.teamside.project.alpha.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepo memberRepo;
    private final AuthService authService;
    private final InquiryRepo inquiryRepo;
    public MemberServiceImpl(MemberRepo memberRepo, AuthService authService, InquiryRepo inquiryRepo) {
        this.memberRepo = memberRepo;
        this.authService = authService;
        this.inquiryRepo = inquiryRepo;
    }

    @Override
    @Transactional
    public JwtTokens sigunUp(MemberDto.SignUpDto signUpDto) throws CustomException {
        checkExistName(signUpDto.getMember().getName());
        checkExistPhone(signUpDto.getMember().getPhone());

        MemberEntity member = new MemberEntity(
                signUpDto.getMember().getName(),
                CryptUtils.encrypt(signUpDto.getMember().getPhone()),
                signUpDto.getMember().getProfileUrl(),
                signUpDto.getMember().getFcmToken(),
                SignUpType.PHONE);


        member.createTerms(new TermsEntity(member,
                signUpDto.getTerms().getTerms(),
                signUpDto.getTerms().getCollect(),
                signUpDto.getTerms().getGps(),
                signUpDto.getTerms().getMarketing(),
                signUpDto.getTerms().getAlarm()));

        JwtTokens jwtTokens = authService.createTokens(member.getMid());

        member.createRefreshToken(jwtTokens.getRefreshToken());

        memberRepo.save(member);
        return jwtTokens;
    }

    @Override
    public void checkExistsName(String name) throws CustomException {
        checkExistName(name);
    }

    @Override
    public void logout() {
        Optional<MemberEntity> member = memberRepo.findByMid(CryptUtils.getMid());
        member.ifPresent(MemberEntity::logout);
    }

    @Override
    public void withdrawal() throws CustomException {
        Optional<MemberEntity> member = memberRepo.findByMid(CryptUtils.getMid());
        if (member.isPresent()) {
            memberRepo.delete(member.get());
        } else throw new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND);

    }

    private void checkExistName(String name) throws CustomException {
        if (memberRepo.existsByName(name)) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
    }

    private void checkExistPhone(String phone) throws CustomException {
        if (memberRepo.existsByPhone(CryptUtils.encrypt(phone))) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_PHONE);
        }
    }

    @Override
    public void inquiry(InquiryDto inquiryDto) {
        InquiryEntity inquiry = new InquiryEntity(
                inquiryDto.getEmail(),
                inquiryDto.getName(),
                inquiryDto.getPlace(),
                inquiryDto.getWorld(),
                inquiryDto.getEtc()
        );

        inquiryRepo.save(inquiry);
    }
}
