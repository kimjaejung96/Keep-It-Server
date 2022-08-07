package com.teamside.project.alpha.member.service.impl;

import com.teamside.project.alpha.member.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.accessToken.validTime}")
    private long accessTokenValidTime;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public String createTokens(String mid) {
        createTokenstemp(mid);
        return null;
    }

    @Override
    public String createAccessToken(String mid) {
        return null;
    }

    @Override
    public String createRefreshToken(String mid) {
        return null;
    }

    private void createTokenstemp(String mid) {
        Claims claims = Jwts.claims().setSubject(mid);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenValidTime*1000*60*60*24))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

    }
}
