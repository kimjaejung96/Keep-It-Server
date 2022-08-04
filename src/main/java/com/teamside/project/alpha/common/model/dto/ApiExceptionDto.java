package com.teamside.project.alpha.common.model.dto;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
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
