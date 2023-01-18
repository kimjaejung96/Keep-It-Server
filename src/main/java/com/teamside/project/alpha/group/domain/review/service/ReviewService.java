package com.teamside.project.alpha.group.domain.review.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;

public interface ReviewService {
    void createReview(String groupId, ReviewDto review) throws CustomException;
    void updateReview(String groupId, ReviewDto.UpdateReviewDto review);

    ReviewDto.ResponseReviewDetail selectReviewDetail(String groupId, String reviewId);

    String createComment(String groupId, CommentDto.CreateComment comment, String reviewId) throws CustomException;

    void keepReview(String groupId, String reviewId) throws CustomException;

    void deleteReview(String groupId, String reviewId) throws CustomException;

    void updateComment(String groupId, CommentDto.CreateComment comment, String reviewId, String commentId);

    void deleteComment(String groupId, String reviewId, String commentId);

    CommentDto.CommentDetail getNextComments(String groupId, String reviewId, Integer nextOffset, int limit);
}
