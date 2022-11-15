package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.group.model.entity.compositeKeys.GroupMemberKeys;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "GROUP_MEMBER_MAPPING")
@IdClass(GroupMemberKeys.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class GroupMemberMappingEntity extends CreateDtEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Id
    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @Column(name = "ORD", columnDefinition = "int")
    private Integer ord;

    @Column(name = "FAVORITE", columnDefinition = "boolean")
    private Boolean favorite;
    @Column(name = "REVIEW_ALARM", columnDefinition = "boolean default false")
    private Boolean reviewAlarm;
    @Column(name = "DAILY_ALARM", columnDefinition = "boolean default false")
    private Boolean dailyAlarm;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private GroupMemberStatus status;

    public GroupMemberMappingEntity(MemberEntity member) {
        this.member = member;
    }

    public GroupMemberMappingEntity(String mid, String groupId) {
        this.mid = mid;
        this.groupId = groupId;
        this.favorite = false;
        this.status = GroupMemberStatus.JOIN;
        this.reviewAlarm = true;
        this.dailyAlarm = true;
    }

    public void updateOrdAndFavorite(Integer ord, Boolean isFavorite) {
        this.ord = ord;
        this.favorite = isFavorite;
    }

    public void updateOrd(Integer ord) {
        this.ord = ord;
    }
    public void updateStatus(GroupMemberStatus status) {
        this.status = status;
        if (status.equals(GroupMemberStatus.EXIT) || status.equals(GroupMemberStatus.WITHDRAWAL)) {
            this.favorite = false;
            this.ord = null;
            this.reviewAlarm = false;
            this.dailyAlarm = false;
        }
    }

    public void updateGroupAlarm(String alarmType) {
        if (alarmType.equals("REVIEW")) {
            this.reviewAlarm = !this.reviewAlarm;
        } else {
            this.dailyAlarm = !this.dailyAlarm;
        }
    }
}
