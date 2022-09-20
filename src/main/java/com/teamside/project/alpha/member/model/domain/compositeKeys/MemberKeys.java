package com.teamside.project.alpha.member.model.domain.compositeKeys;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class MemberKeys implements Serializable {
    private String mid;
    private String targetMid;
}
