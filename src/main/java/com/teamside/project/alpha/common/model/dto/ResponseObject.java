package com.teamside.project.alpha.common.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@NoArgsConstructor
@Getter
public class ResponseObject implements Serializable {
    private Object data;
    private final HttpStatus apiStatus = HttpStatus.OK;

    public void setBody(Object data) {
        this.data = data;
    }
}
