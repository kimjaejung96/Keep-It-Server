package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.entity.SmsLogEntity;
import com.teamside.project.alpha.member.model.enumurate.AuthType;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.repository.SmsLogRepo;
import com.teamside.project.alpha.member.service.AuthService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.accessToken.validTime}")
    private long accessTokenValidTime;

    @Value("${jwt.refreshToken.validTime}")
    private long refreshTokenValidTime;

    @Value("${jwt.secret}")
    private String secretKey;

    private final SmsLogRepo smsLogRepo;

    private final MemberRepo memberRepo;

    public AuthServiceImpl(SmsLogRepo smsLogRepo, MemberRepo memberRepo) {
        this.smsLogRepo = smsLogRepo;
        this.memberRepo = memberRepo;
    }

    @Override
    public JwtTokens createTokens(String mid) {
        Claims claims = Jwts.claims().setSubject(mid);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenValidTime*1000*60))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+refreshTokenValidTime*1000*60))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();


        return new JwtTokens(accessToken, refreshToken);
    }

    @Override
    public String refreshAccessToken(String refreshToken) throws CustomException {
        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        String mid = getAuthPayload(refreshToken);

        MemberEntity member = memberRepo.findByMid(mid).orElseThrow(() -> new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND));

        if (refreshToken.equals(member.getRefreshTokenEntity().getRefreshToken())) {
            Claims claims = Jwts.claims().setSubject(member.getMid());
            Date now = new Date();

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(System.currentTimeMillis()+accessTokenValidTime*1000*60))
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new CustomException(ApiExceptionCode.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public String refreshRefreshToken() throws CustomException {
        MemberEntity member = memberRepo.findByMid(CryptUtils.getMid()).orElseThrow(() -> new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND));

        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(CryptUtils.getMid());

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()+refreshTokenValidTime*1000*60))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        member.getRefreshTokenEntity().changeRefreshToken(refreshToken);

        return refreshToken;
    }

    private Key getSigninKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void tokenValidationCheck(String token) throws CustomException {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigninKey(secretKey)).build()
                .parseClaimsJws(token)
                .getBody();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
            throw new CustomException(ApiExceptionCode.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
            throw new CustomException(ApiExceptionCode.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
            throw new CustomException(ApiExceptionCode.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
            throw new CustomException(ApiExceptionCode.UNAUTHORIZED);
        }
    }

    @Override
    public String getAuthPayload(String token)  {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey(secretKey)).build()
                .parseClaimsJws(token)
                .getBody().get("sub", String.class);
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
    @Transactional
    public JwtTokens checkMember(String phone) throws CustomException {
        MemberEntity member = memberRepo.findByPhoneAndType(CryptUtils.encrypt(phone), SignUpType.PHONE)
                .orElseThrow(() -> new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND));

        JwtTokens jwtTokens = this.createTokens(member.getMid());

        member.getRefreshTokenEntity().changeRefreshToken(jwtTokens.getRefreshToken());

        return jwtTokens;
    }

    @Override
    public void checkPhone(String phone, String authType) throws CustomException {
        Optional<MemberEntity> member = memberRepo.findByPhoneAndType(CryptUtils.encrypt(phone), SignUpType.PHONE);

        if (authType.equals(AuthType.SIGN_UP.getType())) {
            member.ifPresent(m -> {throw new CustomRuntimeException(ApiExceptionCode.MEMBER_ALREADY_EXIST);});
        } else if (authType.equals(AuthType.SIGN_IN.getType())) {
            member.orElseThrow(() -> new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND));
        } else {
            throw new CustomException(ApiExceptionCode.INVALID_AUTH_TYPE);
        }
    }
}
