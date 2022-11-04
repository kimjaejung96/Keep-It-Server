package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.group.model.entity.compositeKeys.MemberFollowKeys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "MEMBER_FOLLOW")
@IdClass(MemberFollowKeys.class)
@DynamicUpdate
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

    //targetMid 팔로우 한 대상 알림 여부
    @Column(name = "ALARM_YN", columnDefinition = "boolean default false")
    private Boolean alarmYn;

    // 팔로우 여부
    @Column(name = "FOLLOW_YN")
    private Boolean followYn;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;


    public MemberFollowEntity(String groupId, String mid, String targetMid) {
        this.groupId = groupId;
        this.mid = mid;
        this.targetMid = targetMid;
        this.alarmYn = false;
        this.followYn = true;
    }

    public void updateFollowStatus() {
        this.followYn = !this.followYn;
    }

}
