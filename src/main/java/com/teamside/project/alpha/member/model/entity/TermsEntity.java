package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@DynamicUpdate
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

    public TermsEntity(MemberEntity member, boolean terms, boolean collect, boolean gps, boolean marketing) {
        this.member = member;
        this.terms = terms;
        this.collect = collect;
        this.gps = gps;
        this.marketing = marketing;
    }

    public TermsEntity() {

    }
    public void updateTerms() {
        this.marketing = !this.marketing;
    }
}
