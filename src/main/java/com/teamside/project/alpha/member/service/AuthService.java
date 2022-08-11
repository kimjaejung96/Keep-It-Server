package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.member.model.dto.JwtTokens;

public interface AuthService {
    JwtTokens getTokens(String mid);
    String createAccessToken(String mid);
    String createRefreshToken(String mid);
}
