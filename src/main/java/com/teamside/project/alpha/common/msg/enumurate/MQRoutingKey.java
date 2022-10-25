package com.teamside.project.alpha.common.msg.enumurate;

import lombok.Getter;

@Getter
public enum MQRoutingKey {
    MARKETING("kps.marketing"),
    GROUP_DELETE("kps.group_delete"),
    GROUP_JOIN("kps.group_join"),
    MY_FOLLOW("kps.my_follow"),
    TEST("kps.test")
    ;

    private final String value;

    MQRoutingKey(String value) {
        this.value = value;
    }
}
