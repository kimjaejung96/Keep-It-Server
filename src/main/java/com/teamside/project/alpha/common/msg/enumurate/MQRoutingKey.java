package com.teamside.project.alpha.common.msg.enumurate;

import lombok.Getter;

@Getter
public enum MQRoutingKey {
    MARKETING("kps.marketing"),
    TEST("kps.test")
    ;

    private final String value;

    MQRoutingKey(String value) {
        this.value = value;
    }
}
