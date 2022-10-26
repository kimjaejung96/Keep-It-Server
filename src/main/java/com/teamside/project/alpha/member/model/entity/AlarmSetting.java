package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "ALARM_SETTING")
public class AlarmSetting extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @OneToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "ALL_SETTING", columnDefinition = "boolean")
    private boolean allSetting;

    @Column(name = "NEW_MEMBER", columnDefinition = "boolean")
    private boolean newMember;

    @Column(name = "NEW_REVIEW", columnDefinition = "boolean")
    private boolean newReview;

    @Column(name = "NEW_DAILY", columnDefinition = "boolean")
    private boolean newDaily;

    @Column(name = "COMMENT", columnDefinition = "boolean")
    private boolean comment;

    @Column(name = "KEEP", columnDefinition = "boolean")
    private boolean keep;

    @Column(name = "JOIN_OUT", columnDefinition = "boolean")
    private boolean joinOut;

    @Column(name = "FOLLOW", columnDefinition = "boolean")
    private boolean follow;

    @OneToMany(mappedBy = "alarmSetting",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewAlarmSetting> reviewAlarmSettings;
}
