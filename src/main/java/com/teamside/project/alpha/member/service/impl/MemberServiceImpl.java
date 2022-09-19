package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.entity.InquiryEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.InquiryRepo;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.service.AuthService;
import com.teamside.project.alpha.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
    public JwtTokens sigunUp(MemberDto.SignUpDto signUpDto) throws CustomException {
        checkExistName(signUpDto.getMember().getName());
        checkExistPhone(signUpDto.getMember().getPhone());

        MemberEntity member = new MemberEntity(signUpDto);

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

    @Override
    public List<MemberDto.InviteMemberList> search(String name, Long groupId) {
        return memberRepo.searchMembers(name, groupId).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public void follow(String targetMid) throws CustomException {
        String mid = CryptUtils.getMid();
        Optional<MemberEntity> targetMember = memberRepo.findByMid(targetMid);
        Optional<MemberEntity> member = memberRepo.findByMid(mid);

        // 이미 팔로중이면 팔로우 취소
        if (member.isPresent() && targetMember.isPresent()) {
            member.get().follow(mid, targetMid);
        } else {
            throw new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void block(String targetMid) throws CustomException {
        String mid = CryptUtils.getMid();
        Optional<MemberEntity> targetMember = memberRepo.findByMid(targetMid);
        Optional<MemberEntity> member = memberRepo.findByMid(mid);

        // 이미 차단중이면 차단 해제
        if (member.isPresent() && targetMember.isPresent()) {
            member.get().block(mid, targetMid);
        } else {
            throw new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND);
        }
    }
}
