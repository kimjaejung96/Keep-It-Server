package com.teamside.project.alpha.member.model.dto;

import com.teamside.project.alpha.member.model.entity.AlarmSetting;
import com.teamside.project.alpha.member.model.entity.TermsEntity;
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
    private boolean marketing;

    public AlarmDto(AlarmSetting alarmSetting, TermsEntity termsEntity) {
        this.allSetting = alarmSetting.isAllSetting();
        this.newMember = alarmSetting.isNewMember();
        this.newReview = alarmSetting.isNewReview();
        this.newDaily = alarmSetting.isNewDaily();
        this.comment = alarmSetting.isComment();
        this.keep = alarmSetting.isKeep();
        this.joinOut = alarmSetting.isJoinOut();
        this.follow = alarmSetting.isFollow();
        this.marketing = termsEntity.isMarketing();
    }
}
