package com.teamside.project.alpha.common.msg.enumurate;

import lombok.Getter;

@Getter
public enum MQRoutingKey {
    MARKETING("kps.marketing"),
    GROUP_DELETE("kps.group_delete"),
    GROUP_JOIN("kps.group_join"),
    MY_FOLLOW("kps.my_follow"),
    NEW_REVIEW("kps.group_new_review"),
    FOLLOW_CONTENTS_REGISTER("kps.follow_contents_registered"),
    TEST("kps.test")
    ;

    private final String value;

    MQRoutingKey(String value) {
        this.value = value;
    }
}
