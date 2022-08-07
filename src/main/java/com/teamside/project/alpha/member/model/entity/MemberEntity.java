package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "MEMBER")
public class MemberEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "PHONE", columnDefinition = "char(11)")
    private String phone;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name = "PIN_PROFILE_URL", columnDefinition = "varchar(255)")
    private String pinProfileUrl;

    @Column(name="FCM_TOKEN", columnDefinition = "varchar(255)")
    private String fcmToken;
}
