package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.entity.SmsLogEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.repository.SmsLogRepo;
import com.teamside.project.alpha.member.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.accessToken.validTime}")
    private long accessTokenValidTime;

    @Value("${jwt.refreshToken.validTime}")
    private long refreshTokenValidTime;

    @Value("${jwt.secret}")
    private String secretKey;

    private SmsLogRepo smsLogRepo;

    private MemberRepo memberRepo;

    public AuthServiceImpl(SmsLogRepo smsLogRepo, MemberRepo memberRepo) {
        this.smsLogRepo = smsLogRepo;
        this.memberRepo = memberRepo;
    }

    @Override
    public JwtTokens getTokens(String mid) {
        return createTokens(mid);
    }

    @Override
    public String createAccessToken(String mid) {
        return null;
    }

    @Override
    public String createRefreshToken(String mid) {
        return null;
    }

    private JwtTokens createTokens(String mid) {
        Claims claims = Jwts.claims().setSubject(mid);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenValidTime*1000*60*60*24))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+refreshTokenValidTime*1000*60*60*24))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();


        return new JwtTokens(accessToken, refreshToken);
    }

    @Override
    public void saveSmsLog(String requestPhoneNum, String number) throws CustomException {
        SmsLogEntity smsLogEntity = SmsLogEntity.builder()
                .phone(CryptUtils.encrypt(requestPhoneNum))
                .authNum(number)
                .build();

        smsLogRepo.save(smsLogEntity);
    }

    @Override
    public void checkAuthNum(SmsAuthDto smsAuthDto) throws CustomException {
        // authNum 5m valid
        SmsLogEntity smsLogEntity = smsLogRepo.findTop1ByPhoneAndCreateTimeBetweenOrderByCreateTimeDesc(
                CryptUtils.encrypt(smsAuthDto.getPhone()),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now())
                .orElseThrow(() -> new CustomException(ApiExceptionCode.AUTH_FAIL));

        // check authNum
        if (!smsLogEntity.getAuthNum().equals(smsAuthDto.getAuthNum())) {
            throw new CustomException(ApiExceptionCode.AUTH_FAIL);
        }
    }

    @Override
    public JwtTokens checkMember(String phone) throws CustomException {
        MemberEntity member = memberRepo.findByPhoneAndType(CryptUtils.encrypt(phone), SignUpType.PHONE)
                .orElseThrow(() -> new CustomException(ApiExceptionCode.USER_NOT_FOUND));

        JwtTokens jwtTokens = this.getTokens(member.getMid());

        return jwtTokens;
    }
}
