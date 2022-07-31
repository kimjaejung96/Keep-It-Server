package com.teamside.project.common.exception;

import com.teamside.project.common.model.dto.ApiExceptionDto;
import lombok.Getter;

@Getter
public class CustomException extends Exception {

    private final ApiExceptionCode apiExceptionCode;

    public CustomException(ApiExceptionCode apiExceptionCode) {
        this.apiExceptionCode = apiExceptionCode;
    }

    public ApiExceptionDto getErrorDetail() {
        return new ApiExceptionDto(this.apiExceptionCode);
    }
}
