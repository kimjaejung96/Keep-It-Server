package com.teamside.project.alpha.common.exception;

import com.teamside.project.alpha.common.model.dto.ApiExceptionDto;
import lombok.Getter;

@Getter
public class CustomRuntimeException extends RuntimeException {

    private final ApiExceptionCode apiExceptionCode;

    public CustomRuntimeException(ApiExceptionCode apiExceptionCode) {
        this.apiExceptionCode = apiExceptionCode;
    }

    public ApiExceptionDto getErrorDetail() {
        return new ApiExceptionDto(this.apiExceptionCode);
    }
}
