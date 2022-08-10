package com.teamside.project.alpha.common.model.dto;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Getter
public class ResponseObject implements Serializable {
    private Object data;
    private ApiStatus apiStatus;

    public void setBody(Object data) {
        this.data = data;
    }

    public ResponseObject(ApiExceptionCode apiExceptionCode) {
        this.apiStatus = new ApiStatus();
        this.apiStatus.apiCode = apiExceptionCode.getApiCode();
        this.apiStatus.message = apiExceptionCode.getMessage();
    }
    @Getter
    private static class ApiStatus {
        private int apiCode;
        private String message;
    }
}
