package com.teamside.project.alpha.member.domain.terms.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;

import javax.persistence.*;

@Entity
@Table(name = "TERMS")
public class TermsEntity extends TimeEntity {
    @Id
    @Column(name = "MID")
    private String mid;

    @MapsId
    @OneToOne(targetEntity = MemberEntity.class)
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "TERMS")
    private boolean terms;
    @Column(name = "COLLECT")
    private boolean collect;
    @Column(name = "MARKETING")
    private boolean marketing;
    @Column(name = "ALARM")
    private boolean alarm;

}
