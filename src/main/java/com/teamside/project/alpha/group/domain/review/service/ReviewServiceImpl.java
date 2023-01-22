package com.teamside.project.alpha.group.domain.review.service;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.msg.MsgService;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.common.util.TransactionUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final GroupRepository groupRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepo memberRepo;
    private final MsgService msgService;
    private final TransactionUtils transactionUtils;


    @Override
    public void createReview(String groupId, ReviewDto review) throws CustomException {
        String mid = CryptUtils.getMid();
        AtomicReference<String> newReviewId = new AtomicReference<>("");
        transactionUtils.runTransaction(() -> {
            checkExistPlace(review.getPlaceId());

            GroupEntity group = selectExistGroup(groupId);

            group.checkExistMember(mid);
            group.checkGroupStatus();

            newReviewId.set(group.createReview(new ReviewEntity(groupId, review)));
        });


        Map<String, Object> newReview = new HashMap<>();
        newReview.put("senderMid", mid);
        newReview.put("groupId", groupId);
        newReview.put("reviewId", newReviewId.get());
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.NEW_REVIEW, newReview);

        Map<String, String> newContent = new HashMap<>();
        newContent.put("senderMid", mid);
        newContent.put("groupId", groupId);
        newContent.put("notiType", "R");
        newContent.put("contentsId", newReviewId.get());
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.FOLLOW_CONTENTS_REGISTER, newContent);
    }

    @Override
    @Transactional
    public void updateReview(String groupId, ReviewDto.UpdateReviewDto review) {
        checkExistPlace(review.getPlaceId());

        GroupEntity group = selectExistGroup(groupId);
        group.checkExistMember(CryptUtils.getMid());
        group.checkGroupStatus();

        ReviewEntity reviewEntity = group.getReviewEntities()
                .stream()
                .filter(r -> r.getReviewId().equals(review.getReviewId()))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

        reviewEntity.checkReviewMaster(CryptUtils.getMid());
        reviewEntity.updateReview(review);
    }

    private void checkExistPlace(Long placeId) {
        if (!placeRepository.existsByPlaceId(placeId)) {
            throw new CustomRuntimeException(ApiExceptionCode.PLACE_NOT_EXIST);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto.ResponseReviewDetail selectReviewDetail(String groupId, String reviewId) {
        GroupEntity group = selectExistGroup(groupId);
        group.checkDeletedReview(reviewId);
        group.checkExistMember(CryptUtils.getMid());
        return groupRepository.selectReviewDetail(groupId, reviewId);
    }

    @Override
    public String createComment(String groupId, CommentDto.CreateComment comment, String reviewId) throws CustomException {
        String mid = CryptUtils.getMid();
        AtomicReference<String>  masterMid = new AtomicReference<>("");
        AtomicReference<String> createdCommentId = new AtomicReference<>("");
        transactionUtils.runTransaction(() -> {
            GroupEntity group = selectExistGroup(groupId);
            group.checkExistMember(mid);
            group.checkGroupStatus();

            ReviewEntity review = group.getReviewEntities().stream()
                    .filter(r -> Objects.equals(r.getReviewId(), reviewId))
                    .findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

            masterMid.set(review.getMasterMid());

            if (comment.getTargetMid() != null && !memberRepo.existsByMid(comment.getTargetMid())) {
                throw new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND);
            }


            if (comment.getParentCommentId() != null && review.getReviewCommentEntities().stream().noneMatch(rc -> Objects.equals(rc.getCommentId(), comment.getParentCommentId()))) {
                throw new CustomRuntimeException(ApiExceptionCode.COMMENT_NOT_ACCESS);
            }

            createdCommentId.set(review.createComment(comment, reviewId).getCommentId());
        });


        if (!masterMid.get().equals(mid)) {
            Map<String, String> myReviewComment = new HashMap<>();
            myReviewComment.put("groupId", groupId);
            myReviewComment.put("reviewId", reviewId);
            myReviewComment.put("commentId", createdCommentId.get());
            msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.MY_REVIEW_COMMENT, myReviewComment);

            if (comment.getParentCommentId() != null) {
                Map<String, String> myCommentComment = new HashMap<>();
                myCommentComment.put("groupId", groupId);
                myCommentComment.put("notiType", "R");
                myCommentComment.put("contentsId", reviewId);
                myCommentComment.put("targetCommentId", comment.getTargetCommentId());
                myCommentComment.put("senderMid", mid);
                myCommentComment.put("newCommentId", createdCommentId.get());
                msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.MY_COMMENT_COMMENT, myCommentComment);
            }
        }

        return createdCommentId.get();
    }

    @Override
    public void keepReview(String groupId, String reviewId) throws CustomException {
        String mid = CryptUtils.getMid();
        AtomicReference<String> masterMid = new AtomicReference<>("");
        AtomicBoolean isNew = new AtomicBoolean(false);
        transactionUtils.runTransaction(() -> {
            GroupEntity group = selectExistGroup(groupId);
            group.checkExistMember(mid);

            ReviewEntity review = group.getReviewEntities().stream()
                    .filter(r -> Objects.equals(r.getReviewId(), reviewId))
                    .findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

            masterMid.set(review.getMasterMid());
            isNew.set(review.keepReview(reviewId, mid));

        });
        if (!masterMid.get().equals(CryptUtils.getMid()) && isNew.get()) {
            Map<String, String> data = new HashMap<>();
            data.put("receiverMid", masterMid.get());
            data.put("senderMid", mid);
            data.put("reviewId", reviewId);
            data.put("groupId", groupId);
            msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.MY_REVIEW_KEEP, data);
        }

    }

    @Override
    @Transactional
    public void deleteReview(String groupId, String reviewId) throws CustomException {
        GroupEntity group = selectExistGroup(groupId);

        group.deleteReview(reviewId);

    }

    @Override
    @Transactional
    public void updateComment(String groupId, CommentDto.CreateComment comment, String reviewId, String commentId) {
        GroupEntity group = selectExistGroup(groupId);
        group.checkGroupStatus();

        ReviewEntity review = group.getReviewEntities().stream().filter(r -> r.getReviewId().equals(reviewId)).findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

        ReviewCommentEntity reviewComment = review.getReviewCommentEntities().stream().filter(c -> c.getCommentId().equals(commentId)).findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.COMMENT_NOT_EXIST));

        reviewComment.updateComment(comment);
    }

    @Override
    @Transactional
    public void deleteComment(String groupId, String reviewId, String commentId) {
        GroupEntity group = selectExistGroup(groupId);

        ReviewEntity review = group.getReviewEntities().stream().filter(r -> r.getReviewId().equals(reviewId)).findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

        ReviewCommentEntity reviewComment = review.getReviewCommentEntities().stream().filter(c -> c.getCommentId().equals(commentId)).findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.COMMENT_NOT_EXIST));

        reviewComment.deleteComment();

    }

    @Override
    public CommentDto.CommentDetail getNextComments(String groupId, String reviewId, Integer nextOffset, int limit) {
        return groupRepository.getReviewCommentDetail(groupId, reviewId, nextOffset, limit);
    }

    private GroupEntity selectExistGroup(String groupId) {
        return groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
    }

}
