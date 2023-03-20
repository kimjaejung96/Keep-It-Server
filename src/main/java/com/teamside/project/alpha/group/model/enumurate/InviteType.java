package com.teamside.project.alpha.group.model.enumurate;

import lombok.Getter;

@Getter
public enum InviteType {
    KAKAO("KAKAO"),
    APP("APP");

    private final String type;

    InviteType(String type) {
        this.type = type;
    }
}
