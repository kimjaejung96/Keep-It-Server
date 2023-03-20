package com.teamside.project.alpha.invite.model.dto;

import com.teamside.project.alpha.group.model.entity.InvitationEntity;

public class InviteDto {
    private String inviteId;

    private String inviter;

    private String groupId;

    private String groupName;

    public InviteDto(InvitationEntity invitation, String inviter, String groupName) {
        this.inviteId = invitation.getInviteId();
        this.inviter = inviter;
        this.groupId = invitation.getGroupId();
        this.groupName = groupName;
    }
}
