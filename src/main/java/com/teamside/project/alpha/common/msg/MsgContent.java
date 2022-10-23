package com.teamside.project.alpha.common.msg;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MsgContent<T> implements Serializable {
    private final T msg;

    public MsgContent(T msg) {
        this.msg = msg;
    }
}
