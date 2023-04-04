package com.teamside.project.alpha.invite.service;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.msg.MsgService;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.common.util.TransactionUtils;
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
    private final TransactionUtils transactionUtils;

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

    @Override
    @Transactional(readOnly = true)
    public InviteDto.InviteInfo getInvite(String inviteId, String groupId) {
        GroupEntity group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        InvitationEntity invitationEntity = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.INVITE_NOT_EXIST));

        MemberEntity inviter = memberRepo.findByMid(invitationEntity.getMid())
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));

        MemberEntity member = memberRepo.findByMid(CryptUtils.getMid())
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));

        return new InviteDto.InviteInfo(invitationEntity, inviter.getName(), member.getName(), group);
    }

    @Override
    @Transactional
    public void inviteJoin(String inviteId, String groupId) {
        String mid = CryptUtils.getMid();

        transactionUtils.runTransaction(() -> {
            InvitationEntity invitation = inviteRepository.findById(inviteId)
                    .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.INVITE_NOT_EXIST));

            // check expire and groupId
            invitation.checkInvitation(groupId);

            GroupEntity group = groupRepository.findByGroupId(groupId)
                    .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

            // check already join / delete / memberQuantity
            group.inviteJoinPossible(group);

            group.addMember(mid);
        });

        Map<String, String> data = new HashMap<>();
        data.put("senderMid", mid);
        data.put("groupId", groupId);
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.GROUP_JOIN, data);
    }
}
