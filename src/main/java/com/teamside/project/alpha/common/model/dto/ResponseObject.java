package com.teamside.project.alpha.common.model.dto;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Getter
public class ResponseObject implements Serializable {
    private Object data;
    private ApiExceptionCode apiCode = ApiExceptionCode.OK;

    public void setBody(Object data) {
        this.data = data;
    }

    public ResponseObject(ApiExceptionCode apiExceptionCode) {
        this.apiCode = apiExceptionCode;
    }
}
