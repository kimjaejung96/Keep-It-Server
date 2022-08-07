package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER")
public class MemberEntity extends TimeEntity {
    @Id
    @Column(name = "MID")
    private String mid;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PROFILE_URL")
    private String profileUrl;

    @Column(name = "PIN_PROFILE_URL")
    private String pinProfileUrl;

    @Column(name="FCM_TOKEN")
    private String fcmToken;
}
