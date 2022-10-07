package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.domain.DailyCommentEntity;
import com.teamside.project.alpha.group.model.domain.DailyEntity;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.DailyService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class DailyServiceImpl implements DailyService {
    private final GroupRepository groupRepository;

    public DailyServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public void createDaily(Long groupId, DailyDto dailyDto) throws CustomException {
        GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        groupEntity.checkExistMember(CryptUtils.getMid());

        groupEntity.createDaily(new DailyEntity(groupId, dailyDto));
    }

    @Override
    @Transactional
    public void updateDaily(Long groupId, DailyDto.UpdateDailyDto dailyDto) throws CustomException {
        GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        groupEntity.checkExistMember(CryptUtils.getMid());

        DailyEntity dailyEntity = groupEntity.getDailyEntities()
                .stream()
                .filter(d -> d.getDailyId().equals(dailyDto.getDailyId()))
                .findFirst()
                .orElseThrow((() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST)));
        dailyEntity.checkDailyMaster(CryptUtils.getMid());
        dailyEntity.updateDaily(dailyDto);
    }

    @Override
    public DailyDto.ResponseDailyDetail selectDaily(Long groupId, Long dailyId) {
        return groupRepository.selectDaily(groupId, dailyId);
    }

    @Override
    @Transactional
    public void deleteDaily(Long groupId, Long dailyId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        group.deleteDaily(dailyId);
    }

    @Override
    @Transactional
    public void createComment(Long groupId, Long dailyId, CommentDto.CreateComment comment) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(CryptUtils.getMid());

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        if (comment.getParentCommentId() != null) {
            daily.getDailyCommentEntities().stream()
                    .filter(c -> Objects.equals(c.getCommentId(), comment.getParentCommentId()))
                    .findAny()
                    .orElseThrow(() -> new CustomException(ApiExceptionCode.COMMENT_NOT_ACCESS));
        }

        daily.createComment(comment, dailyId);
    }

    @Override
    @Transactional
    public void updateComment(Long groupId, Long dailyId, Long commentId, CommentDto.CreateComment comment) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        DailyCommentEntity commentEntity = daily.getDailyCommentEntities().stream()
                .filter(c -> Objects.equals(c.getCommentId(), commentId))
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.OK));

        commentEntity.checkCommentMaster(mid);
        commentEntity.updateComment(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long groupId, Long dailyId, Long commentId) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        DailyCommentEntity commentEntity = daily.getDailyCommentEntities().stream()
                .filter(c -> Objects.equals(c.getCommentId(), commentId))
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.OK));

        commentEntity.checkCommentMaster(mid);
        commentEntity.deleteComment();
    }

    @Override
    @Transactional
    public void keepDaily(Long groupId, Long dailyId) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);

        DailyEntity daily = group.getDailyEntities().stream()
                .filter(d -> Objects.equals(d.getDailyId(), dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        daily.keepDaily(dailyId, mid);
        // 메소드있지않나 ㄷ;
    }
}
