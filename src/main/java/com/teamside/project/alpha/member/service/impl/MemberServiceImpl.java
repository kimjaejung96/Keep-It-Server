package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.member.domain.terms.model.entity.RefreshTokenEntity;
import com.teamside.project.alpha.member.domain.terms.model.entity.TermsEntity;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
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
    public boolean checkId(String name)  {
        return memberRepo.existsByName(name);
    }


    @Override
    @Transactional
    public String sigunUp(MemberDto.SignUpDto signUpDto) {
        MemberEntity member = MemberEntity.builder()
                .mid(generateMid(signUpDto.getMember().getPhone()))
                .name(signUpDto.getMember().getName())
                .phone(signUpDto.getMember().getPhone())
                .profileUrl(signUpDto.getMember().getProfileUrl())
                .pinProfileUrl(signUpDto.getMember().getPinProfileUrl())
                .build();

        member.changeTerms(TermsEntity.builder()
                .member(member)
                        .terms(signUpDto.getTerms().isTerms())
                        .collect(signUpDto.getTerms().isCollect())
                        .marketing(signUpDto.getTerms().isMarketing())
                        .alarm(signUpDto.getTerms().isAlarm())
                .build());

        JwtTokens jwtTokens = authService.getTokens(member.getMid());

        member.changeRefreshToken(RefreshTokenEntity.builder()
                        .member(member)
                        .refreshToken(jwtTokens.getRefreshToken())
                .build());

        memberRepo.save(member);

        return jwtTokens.getAccessToken();
    }

    private String generateMid(String phone) {
        return "PH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + phone.substring(3);
    }
}
