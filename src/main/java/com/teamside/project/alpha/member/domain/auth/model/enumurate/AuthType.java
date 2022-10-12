package com.teamside.project.alpha.member.domain.auth.model.enumurate;


public enum AuthType {

    NULL("null"),
    SIGN_UP("sign-up"),
    SIGN_IN("sign-in");

    private final String type;

    AuthType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
