package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.group.model.entity.compositeKeys.MemberFollowKeys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "MEMBER_FOLLOW")
@IdClass(MemberFollowKeys.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFollowEntity extends CreateDtEntity {
    @Id
    @Column(name = "GROUP_ID")
    private String groupId;
    @Id
    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @Id
    @Column(name = "TARGET_MID", columnDefinition = "char(36)")
    private String targetMid;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;


    public MemberFollowEntity(String groupId, String mid, String targetMid) {
        this.groupId = groupId;
        this.mid = mid;
        this.targetMid = targetMid;
    }
}
