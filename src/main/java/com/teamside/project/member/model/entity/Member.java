package com.teamside.project.member.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {
    @Id
    @Column(name = "UID")
    private String id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PROFILE")
    private String profile;
    @Column(name = "PIN_PROFILE")
    private String pinProfile;

}
