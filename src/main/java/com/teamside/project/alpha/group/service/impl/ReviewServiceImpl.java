package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.domain.ReviewEntity;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.ReviewService;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final GroupRepository groupRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepo memberRepo;

    public ReviewServiceImpl(GroupRepository groupRepository, PlaceRepository placeRepository, MemberRepo memberRepo) {
        this.groupRepository = groupRepository;
        this.placeRepository = placeRepository;
        this.memberRepo = memberRepo;
    }

    @Override
    @Transactional
    public void createReview(Long groupId, ReviewDto review) {
        checkExistPlace(review.getPlaceId());

        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(CryptUtils.getMid());
        group.checkExistReview(review.getPlaceId());

        group.createReview(new ReviewEntity(groupId, review));
    }

    @Override
    @Transactional
    public void updateReview(Long groupId, ReviewDto.UpdateReviewDto review) {
        checkExistPlace(review.getPlaceId());

        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(CryptUtils.getMid());

        ReviewEntity reviewEntity = group.getReviewEntities()
                .stream()
                .filter(r -> r.getReviewId().equals(review.getReviewId()))
                .findFirst()
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
    public ReviewDto.ResponseReviewDetail selectReviewDetail(Long groupId, Long reviewId) {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(CryptUtils.getMid());
        return groupRepository.selectReviewDetail(groupId, reviewId);
    }

    @Override
    @Transactional
    public void createComment(Long groupId, CommentDto.CreateComment comment, Long reviewId) {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(CryptUtils.getMid());

        ReviewEntity review = group.getReviewEntities().stream()
                .filter(r -> Objects.equals(r.getReviewId(), reviewId))
                .findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

        if (comment.getTargetMid() != null && !memberRepo.existsByMid(comment.getTargetMid())) {
            throw new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND);
        }


        if (comment.getParentCommentId() != null && review.getReviewCommentEntities().stream().noneMatch(rc -> Objects.equals(rc.getCommentId(), comment.getParentCommentId()))) {
            throw new CustomRuntimeException(ApiExceptionCode.COMMENT_NOT_ACCESS);
        }
//        if (comment.getParentCommentId() != null) {
//            review.getReviewCommentEntities().stream()
//                    .filter(rc -> Objects.equals(rc.getCommentId(), comment.getParentCommentId()))
//                    .findAny()
//                    .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.COMMENT_NOT_ACCESS));
//        }

        review.createComment(comment, reviewId);

    }

    @Override
    @Transactional
    public void keepReview(Long groupId, Long reviewId) {
        String mid = CryptUtils.getMid();
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(mid);

        ReviewEntity review = group.getReviewEntities().stream()
                .filter(r -> Objects.equals(r.getReviewId(), reviewId))
                .findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

        review.keepReview(reviewId, mid);
    }

    @Override
    @Transactional
    public void deleteReview(Long groupId, Long reviewId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        group.deleteReview(reviewId);

    }
}
