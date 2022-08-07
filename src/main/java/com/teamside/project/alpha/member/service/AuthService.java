package com.teamside.project.alpha.member.service;

import org.springframework.stereotype.Service;

public interface AuthService {
    String createTokens(String mid);
    String createAccessToken(String mid);
    String createRefreshToken(String mid);
}
