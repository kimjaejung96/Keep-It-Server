package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.service.AuthService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.accessToken.validTime}")
    private long accessTokenValidTime;

    @Value("${jwt.refreshToken.validTime}")
    private long refreshTokenValidTime;

    @Value("${jwt.secret}")
    private String secretKey;

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

    private Key getSigninKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void tokenValidationCheck(String token) throws CustomException {
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
}
