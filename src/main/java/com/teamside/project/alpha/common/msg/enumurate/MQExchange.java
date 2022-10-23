package com.teamside.project.alpha.common.msg.enumurate;

import lombok.Getter;

@Getter
public enum MQExchange {
    KPS_EXCHANGE("kps.exchange")
    ;

    private final String value;

    MQExchange(String value) {
        this.value = value;
    }
}
