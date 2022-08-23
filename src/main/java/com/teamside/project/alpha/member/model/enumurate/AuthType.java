package com.teamside.project.alpha.member.model.enumurate;

public enum AuthType {

    SIGN_UP("sign-up"),
    SIGN_IN("sign-in");

    private String type;

    AuthType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
