package com.teamside.project.alpha.invite.service;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.msg.MsgService;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.entity.InvitationEntity;
import com.teamside.project.alpha.group.model.enumurate.InviteType;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.invite.model.dto.InviteDto;
import com.teamside.project.alpha.invite.repository.InviteRepository;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final InviteRepository inviteRepository;
    private final GroupRepository groupRepository;
    private final MemberRepo memberRepo;
    private final MsgService msgService;

    @Override
    @Transactional
    public InviteDto inviteMember(String groupId, InviteType inviteType, String mid) throws CustomRuntimeException {
        GroupEntity group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        MemberEntity inviter = memberRepo.findByMid(CryptUtils.getMid())
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));

        InvitationEntity invitation = new InvitationEntity(groupId, inviteType, mid);
        inviteRepository.save(invitation);

        // MQ
        if (inviteType.equals(InviteType.APP)) {
            Map<String, Object> data = new HashMap<>();

            data.put("receiverMid", mid);
            data.put("senderMid", CryptUtils.getMid());
            data.put("groupId", groupId);
            data.put("inviteId", invitation.getInviteId());

            msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.GROUP_INVITE, data);
        }

        return new InviteDto(invitation, inviter.getName(), group.getName());
    }

}
