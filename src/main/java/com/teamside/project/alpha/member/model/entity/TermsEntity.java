package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "TERMS")
public class TermsEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @OneToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "TERMS", columnDefinition = "boolean")
    private boolean terms;

    @Column(name = "COLLECT", columnDefinition = "boolean")
    private boolean collect;

    @Column(name = "GPS", columnDefinition = "boolean")
    private boolean gps;
    @Column(name = "MARKETING", columnDefinition = "boolean")
    private boolean marketing;

    @Column(name = "ALARM", columnDefinition = "boolean")
    private boolean alarm;

    public TermsEntity(MemberEntity member, boolean terms, boolean collect, boolean gps, boolean marketing, boolean alarm) {
        this.member = member;
        this.terms = terms;
        this.collect = collect;
        this.gps = gps;
        this.marketing = marketing;
        this.alarm = alarm;
    }

    public TermsEntity() {

    }
}
