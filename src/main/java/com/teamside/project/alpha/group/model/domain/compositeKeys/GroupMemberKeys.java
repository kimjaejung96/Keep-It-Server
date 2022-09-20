package com.teamside.project.alpha.group.model.domain.compositeKeys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class GroupMemberKeys implements Serializable {
    private String mid;
    private Long groupId;

}
