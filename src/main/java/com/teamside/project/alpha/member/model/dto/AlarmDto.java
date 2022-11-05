package com.teamside.project.alpha.member.model.dto;

import com.teamside.project.alpha.member.model.entity.AlarmSetting;
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

    public AlarmDto(AlarmSetting alarmSetting) {
        this.allSetting = alarmSetting.isAllSetting();
        this.newMember = alarmSetting.isNewMember();
        this.newReview = alarmSetting.isNewReview();
        this.newDaily = alarmSetting.isNewDaily();
        this.comment = alarmSetting.isComment();
        this.keep = alarmSetting.isKeep();
        this.joinOut = alarmSetting.isJoinOut();
        this.follow = alarmSetting.isFollow();
    }
}
