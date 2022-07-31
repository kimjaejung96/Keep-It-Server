package com.teamside.project.common.model.dto;

import com.teamside.project.common.exception.ApiExceptionCode;
import lombok.Getter;

@Getter
public class ApiExceptionDto {
    private final long errorCode;
    private final String errorMessage;

    public ApiExceptionDto(ApiExceptionCode apiExceptionCode) {
        this.errorCode = apiExceptionCode.getApiCode();
        this.errorMessage = apiExceptionCode.getMessage();
    }
}
