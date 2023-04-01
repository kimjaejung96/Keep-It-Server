package com.teamside.project.alpha.invite.model.dto;

import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.entity.InvitationEntity;
import com.teamside.project.alpha.group.model.enumurate.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InviteDto {
    private String inviteId;

    private String inviter;

    private String groupId;

    private String groupName;

    private Boolean expireYn;

    public InviteDto(InvitationEntity invitation, String inviter, String groupName) {
        this.inviteId = invitation.getInviteId();
        this.inviter = inviter;
        this.groupId = invitation.getGroupId();
        this.groupName = groupName;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class InviteInfo extends InviteDto {
        private String memberName;
        private String profileUrl;
        private Category category;
        private Long participantCount;
        private Boolean expireYn;

        public InviteInfo(InvitationEntity invitation, String inviter, String memberName, GroupEntity groupEntity) {
            super(invitation, inviter, groupEntity.getName());
            this.memberName = memberName;
            this.profileUrl = groupEntity.getProfileUrl();
            this.category = groupEntity.getCategory();
            this.participantCount = groupEntity.getParticipantCount();
            this.expireYn = LocalDateTime.now().isAfter(invitation.getExpireTime());
        }
    }

}
