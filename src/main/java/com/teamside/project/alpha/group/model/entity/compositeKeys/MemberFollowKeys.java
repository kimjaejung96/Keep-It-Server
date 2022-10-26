package com.teamside.project.alpha.group.model.entity.compositeKeys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class MemberFollowKeys implements Serializable {
    private String mid;
    private String targetMid;
    private String groupId;
}
