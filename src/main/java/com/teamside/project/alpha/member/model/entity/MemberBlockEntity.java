package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.member.model.entity.compositeKeys.MemberKeys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "MEMBER_BLOCK")
@IdClass(MemberKeys.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBlockEntity extends CreateDtEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @Id
    @Column(name = "TARGET_MID", columnDefinition = "char(36)")
    private String targetMid;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity targetMember;

    public MemberBlockEntity(String mid, String targetMid) {
        this.mid = mid;
        this.targetMid = targetMid;
    }
}
