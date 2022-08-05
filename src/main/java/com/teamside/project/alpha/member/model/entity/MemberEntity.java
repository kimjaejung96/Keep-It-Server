package com.teamside.project.alpha.member.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MemberEntity {
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
