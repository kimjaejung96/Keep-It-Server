package com.teamside.project.alpha.common.exception;

import lombok.Getter;

@Getter
public enum ApiExceptionCode {
    NONE (0,""),
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    INTERNAL_ERROR(500, "Internal Error"),
    DUPLICATE_NAME(600, "Name is Duplicated"),
    DUPLICATE_PHONE(601, "Phone Number is Duplicated"),
    MEMBER_NOT_FOUND(700, "Member not found"),
    AUTH_FAIL(701, "Auth fail"),
    MEMBER_ALREADY_EXIST(702, "Member already exists"),
    INVALID_AUTH_TYPE(703, "Invalid auth type"),
    GROUP_NOT_FOUND(800, "Group Not Found"),
    SYSTEM_ERROR(999, "System Error"),
    ;


    private final int apiCode;
    private final String message;

    ApiExceptionCode(int code, String message) {
        this.apiCode = code;
        this.message = message;
    }
}
