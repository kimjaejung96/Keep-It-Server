package com.teamside.project.alpha.member.domain.auth.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokens {
    private final String accessToken;
    private final String refreshToken;

    public JwtTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
