package com.teamside.project.alpha.group.domain.daily.service;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.msg.MsgService;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.common.util.TransactionUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyCommentEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DailyServiceImpl implements DailyService {
    private final GroupRepository groupRepository;
    private final MemberRepo memberRepo;
    private final MsgService msgService;
    private final TransactionUtils transactionUtils;


    @Override
    public void createDaily(String groupId, DailyDto dailyDto) throws CustomException {
        AtomicReference<String> createdDailyId = new AtomicReference<>("");
        transactionUtils.runTransaction(() -> {
            GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
            groupEntity.checkExistMember(CryptUtils.getMid());
            groupEntity.checkGroupStatus();
            createdDailyId.set(groupEntity.createDaily(new DailyEntity(groupId, dailyDto)));
        });



        Map<String, String> data = new HashMap<>();
        data.put("senderMid", CryptUtils.getMid());
        data.put("groupId", groupId);
        data.put("dailyId", createdDailyId.get());
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.GROUP_NEW_DAILY, data);
    }

    @Override
    @Transactional
    public void updateDaily(String groupId, DailyDto.UpdateDailyDto dailyDto) throws CustomException {
        GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        groupEntity.checkExistMember(CryptUtils.getMid());
        groupEntity.checkGroupStatus();

        DailyEntity dailyEntity = groupEntity.getDailyEntities()
                .stream()
                .filter(d -> d.getDailyId().equals(dailyDto.getDailyId()))
                .findAny()
                .orElseThrow((() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST)));
        dailyEntity.checkDailyMaster(CryptUtils.getMid());
        dailyEntity.updateDaily(dailyDto);
    }

    @Override
    @Transactional
    public DailyDto.ResponseDailyDetail selectDaily(String groupId, String dailyId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkDeletedDaily(dailyId);
        group.checkExistMember(CryptUtils.getMid());

        return groupRepository.selectDaily(groupId, dailyId);
    }

    @Override
    @Transactional
    public void deleteDaily(String groupId, String dailyId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        group.deleteDaily(dailyId);
    }

    @Override
    public String createComment(String groupId, String dailyId, CommentDto.CreateComment comment) throws CustomException {
        String mid = CryptUtils.getMid();
        AtomicReference<String> masterMid = new AtomicReference<>("");
        AtomicReference<String> commentId = new AtomicReference<>("");
        transactionUtils.runTransaction(() -> {
            GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
            group.checkExistMember(mid);
            group.checkGroupStatus();

            DailyEntity daily = group.getDailyEntities().stream()
                    .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                    .findAny()
                    .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

            masterMid.set(daily.getMasterMid());

            if (comment.getParentCommentId() != null) {
                daily.getDailyCommentEntities().stream()
                        .filter(c -> Objects.equals(c.getCommentId(), comment.getParentCommentId()))
                        .findAny()
                        .orElseThrow(() -> new CustomException(ApiExceptionCode.COMMENT_NOT_ACCESS));
            }

            commentId.set(daily.createComment(comment, dailyId));
        });


        if (!masterMid.get().equals(mid)) {
            memberRepo.findByMid(masterMid.get()).ifPresent( m -> {
                if (m.getAlarmSetting().isAllSetting() && m.getAlarmSetting().isComment()) {
                    Map<String, Object> newComment = new HashMap<>();
                    newComment.put("dailyId", dailyId);
                    newComment.put("groupId", groupId);
                    newComment.put("commentId", commentId.get());
                    msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.MY_DAILY_COMMENT, newComment);
                }
            });
        }
        if (comment.getParentCommentId() != null) {
            Map<String, String> data = new HashMap<>();
            data.put("groupId", groupId);
            data.put("notiType", "D");
            data.put("contentsId", dailyId);
            data.put("targetCommentId", comment.getTargetCommentId());
            data.put("senderMid", mid);
            data.put("newCommentId", commentId.get());
            msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.MY_COMMENT_COMMENT, data);
        }

        return commentId.get();
    }

    @Override
    @Transactional
    public void updateComment(String groupId, String dailyId, String commentId, CommentDto.CreateComment comment) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);
        group.checkGroupStatus();

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        DailyCommentEntity commentEntity = daily.getDailyCommentEntities().stream()
                .filter(c -> Objects.equals(c.getCommentId(), commentId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.OK));

        commentEntity.checkCommentMaster(mid);
        commentEntity.updateComment(comment);
    }

    @Override
    @Transactional
    public void deleteComment(String groupId, String dailyId, String commentId) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        DailyCommentEntity commentEntity = daily.getDailyCommentEntities().stream()
                .filter(c -> Objects.equals(c.getCommentId(), commentId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.OK));

        commentEntity.checkCommentMaster(mid);
        commentEntity.deleteComment();
    }

    @Override
    @Transactional
    public void keepDaily(String groupId, String dailyId) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);
        group.checkGroupStatus();

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        daily.keepDaily(dailyId, mid);
    }
}
