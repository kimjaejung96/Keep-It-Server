package com.teamside.project.alpha.member.model.entity.compositeKeys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class MemberBlockKeys implements Serializable {
    private String mid;
    private String targetMid;
}
