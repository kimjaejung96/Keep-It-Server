package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.member.domain.terms.model.entity.RefreshTokenEntity;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepo memberRepo;
    private final AuthService authService;

    public MemberServiceImpl(MemberRepo memberRepo, AuthService authService) {
        this.memberRepo = memberRepo;
        this.authService = authService;
    }

    @Override
    public boolean existName(String name)  {
        return memberRepo.existsByName(name);
    }

    @Override
    public boolean existPhone(String phone) {
        return memberRepo.existsByPhone(phone);
    }

    @Override
    @Transactional
    public JwtTokens sigunUp(MemberDto.SignUpDto signUpDto) {
        MemberEntity member = new MemberEntity(
                generateMid(signUpDto.getMember().getPhone()),
                signUpDto.getMember().getName(),
                signUpDto.getMember().getPhone(),
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

        member.changeRefreshToken(RefreshTokenEntity.builder()
                        .member(member)
                        .refreshToken(jwtTokens.getRefreshToken())
                .build());

        memberRepo.save(member);

        return jwtTokens;
    }

    private String generateMid(String phone) {
        return "PH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + phone.substring(3);
    }
}
