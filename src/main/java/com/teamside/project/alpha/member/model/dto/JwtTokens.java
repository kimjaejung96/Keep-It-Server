package com.teamside.project.alpha.member.model.dto;

import lombok.Getter;

@Getter
public class JwtTokens {
    private String accessToken;
    private String refreshToken;

    public JwtTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
