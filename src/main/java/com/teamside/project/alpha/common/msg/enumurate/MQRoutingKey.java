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
    MY_REVIEW_COMMENT("kps.my_review_comment"),
    MY_REVIEW_KEEP("kps.my_review_keep"),
    MY_DAILY_COMMENT("kps.my_daily_comment"),
    MY_COMMENT_COMMENT("kps.my_comment_comment"),
    GROUP_NEW_DAILY("kps.group_new_daily"),
    GROUP_EXPELLED("kps.group_expelled"),

    TEST("kps.test")
    ;

    private final String value;

    MQRoutingKey(String value) {
        this.value = value;
    }
}
