package com.teamside.project.alpha.member.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {
    @Id
    @Column(name = "MID")
    private String mid;
    @Column(name = "NAME")
    private String name;
    @Column(name = "NICKNAME")
    private String nickName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "PROFILE")
    private String profile;
    @Column(name = "PIN_PROFILE")
    private String pinProfile;

}
