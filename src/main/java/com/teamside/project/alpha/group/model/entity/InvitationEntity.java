package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.enumurate.InviteType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvitationEntity extends CreateDtEntity {

    @Id
    @Column(name = "INVITE_ID", columnDefinition = "char(36)")
    private String inviteId;

    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private InviteType inviteType;

    @Column(name = "TARGET_MID", columnDefinition = "char(36)")
    private String targetMid;

    public InvitationEntity(String groupId, InviteType inviteType, String targetMid) {
        this.inviteId = UUID.randomUUID().toString();
        this.mid = CryptUtils.getMid();
        this.groupId = groupId;
        this.inviteType = inviteType;
        this.targetMid = targetMid;
    }
}
