package com.teamside.project.alpha.member.domain.mypage.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.enumurate.CommentStatus;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewCommentEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.domain.auth.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.domain.auth.model.entity.SmsLogEntity;
import com.teamside.project.alpha.member.domain.auth.repository.SmsLogRepo;
import com.teamside.project.alpha.member.domain.mypage.model.dto.*;
import com.teamside.project.alpha.member.domain.mypage.model.enumurate.MyGroupManagementType;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.InquiryRepo;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {
    private final MemberRepo memberRepo;
    private final GroupRepository groupRepository;
    private final InquiryRepo inquiryRepo;
    private final SmsLogRepo smsLogRepo;

    @Override
    @Transactional
    public MyPageHome getMyPageHome() {
        List<String> blocks = memberRepo.findByMid(CryptUtils.getMid())
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND))
                .getBlockTarget();

        return memberRepo.getMyPageHome(blocks);
    }

    @Override
    public List<MyGroups> getMyGroups() {
        return groupRepository.getMyGroups();
    }

    @Override
    public MyReviews getMyReviews(String groupId, Long lastSeq, Long pageSize) {
        List<MyReviews.Reviews> myReviews =  groupRepository.getMyReviews(groupId, lastSeq, pageSize);
        myReviews.stream().filter(d -> d.getImageUrl() != null).forEach(d -> d.splitImages(d.getImageUrl()));

        return new MyReviews(myReviews, pageSize);
    }

    @Override
    public MyDaily getMyDaily(String groupId, Long lastSeq, Long pageSize) {
        List<MyDaily.Daily> myDaily = groupRepository.getMyDaily(groupId, lastSeq, pageSize);

        return new MyDaily(myDaily, pageSize);
    }

    @Override
    public MyKeep getKeepMyReviews(Long nextOffset, Long pageSize) {
        List<MyKeep.KeepReview> reviews = groupRepository.getKeepMyReviews(nextOffset, pageSize);
        reviews.stream().filter(d -> d.getImageUrl() != null).forEach(d -> d.splitImages(d.getImageUrl()));

        return new MyKeep(reviews, null, nextOffset, pageSize);
    }

    @Override
    public MyKeep getKeepMyDaily(Long nextOffset, Long pageSize) {
        List<MyKeep.KeepDaily> keepDaily = groupRepository.getKeepMyDaily(nextOffset, pageSize);

        return new MyKeep(null, keepDaily, nextOffset, pageSize);
    }

    @Override
    public MyComments getMyComments(String groupId, Long offset, Long pageSize) {
        List<MyComments.comments> myComments = groupRepository.getMyComments(groupId, offset, pageSize);

        return new MyComments(myComments);
    }

    @Override
    public MyFollowingDto getMyFollowingList(Long nextOffset, Long pageSize) {
        return groupRepository.getMyFollowingDto(nextOffset, pageSize);
    }

    @Override
    public MyBlock getMyBlocks(Long nextOffset, Long pageSize) {
        List<MyBlock.Block> blocks = groupRepository.getMyBlocks(nextOffset, pageSize);
        return new MyBlock(blocks, nextOffset, pageSize);
    }

    @Override
    @Transactional
    public void editKeep(MyKeep.editKeep editKeep) throws CustomException {
        if (!editKeep.getIsAll() && editKeep.getKeepSeqList() == null) {
            throw new CustomException(ApiExceptionCode.BAD_REQUEST);
        }
        String type = editKeep.getType() == null ? "" : editKeep.getType();

        if (type.equals("REVIEW")) {
            groupRepository.editReviewKeep(editKeep);
        } else if (type.equals("DAILY")) {
            groupRepository.editDailyKeep(editKeep);
        } else {
            throw new CustomException(ApiExceptionCode.INVALID_VIEW_TYPE);
        }
    }

    @Override
    public void myInquiry(InquiryDto.MyInquiry myInquiry) {
        inquiryRepo.save(myInquiry.toEntity());
    }

    @Override
    public MyGroupManagement getMyGroupsManagements(MyGroupManagementType type) {
        List<MyGroupManagement.Group> data = groupRepository.getMyGroupsManagements(type);
        data.forEach(MyGroupManagement.Group::changeDeleteDt);

        return new MyGroupManagement(data);
    }

    @Override
    @Transactional
    public void deleteMyWritings(String groupId) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.getReviewEntities().stream()
                .filter(d -> !d.getIsDelete())
                .forEach(d -> {
                    d.getReviewCommentEntities().stream()
                            .filter(c -> c.getMasterMid().equals(mid))
                            .filter(comment -> comment.getStatus().equals(CommentStatus.CREATED))
                            .forEach(ReviewCommentEntity::deleteComment);
                    if (d.getMasterMid().equals(mid)) {
                        d.deleteReview();
                    }
                });
        group.getDailyEntities().stream()
                .filter(d -> !d.getIsDelete())
                .forEach(d -> {
                    d.getDailyCommentEntities().stream()
                            .filter(c -> c.getMasterMid().equals(mid))
                            .filter(comment -> comment.getStatus().equals(CommentStatus.CREATED))
                            .forEach(DailyCommentEntity::deleteComment);
                    if (d.getMasterMid().equals(mid)) {
                        d.deleteDaily();
                    }
                });
    }

    @Override
    @Transactional
    public void changePhone(SmsAuthDto smsAuthDto) throws CustomException {
        // authNum 5m valid
        SmsLogEntity smsLogEntity = smsLogRepo.findTop1ByPhoneAndCreateTimeBetweenOrderByCreateTimeDesc(
                        smsAuthDto.getPhone(),
                        LocalDateTime.now().minusMinutes(5),
                        LocalDateTime.now())
                .orElseThrow(() -> new CustomException(ApiExceptionCode.AUTH_FAIL));

        // check authNum
        if (!smsLogEntity.getAuthNum().equals(smsAuthDto.getAuthNum())) {
            throw new CustomException(ApiExceptionCode.AUTH_FAIL);
        }
        MemberEntity member = memberRepo.findByMid(CryptUtils.getMid()).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));
        member.changePhoneNumber(smsAuthDto.getPhone());
    }
}
