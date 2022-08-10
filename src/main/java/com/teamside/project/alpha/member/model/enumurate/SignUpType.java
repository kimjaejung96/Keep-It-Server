package com.teamside.project.alpha.member.model.enumurate;

public enum SignUpType {
    PHONE("phone"),
    NAVER("naver"),
    KAKAO("kakao"),
    GOOGLE("google"),
    ;

    private String type;

    SignUpType(String type) {
    }
}
