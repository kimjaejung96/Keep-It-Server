package com.teamside.project.alpha.member.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmDto {
    private boolean allSetting;
    private boolean newMember;
    private boolean newReview;
    private boolean newDaily;
    private boolean comment;
    private boolean keep;
    private boolean joinOut;
    private boolean follow;
}
