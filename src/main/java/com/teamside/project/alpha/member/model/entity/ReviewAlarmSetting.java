package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.member.model.entity.compositeKeys.AlarmSettingKey;
import lombok.Getter;

import javax.persistence.*;
@Entity
@Getter
@IdClass(AlarmSettingKey.class)
@Table(name = "REVIEW_ALARM_SETTING")
public class ReviewAlarmSetting {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private AlarmSetting alarmSetting;

    @Id
    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

    @Column(name = "ALARM_YN", columnDefinition = "boolean")
    private Boolean alarmYN;
}
