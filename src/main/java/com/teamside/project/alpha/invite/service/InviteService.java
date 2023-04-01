package com.teamside.project.alpha.invite.service;

import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.group.model.enumurate.InviteType;
import com.teamside.project.alpha.invite.model.dto.InviteDto;

public interface InviteService {
    InviteDto inviteMember(String groupId, InviteType inviteType, String mid) throws CustomRuntimeException;

    InviteDto.InviteInfo getInvite(String inviteId, String groupId);
}
