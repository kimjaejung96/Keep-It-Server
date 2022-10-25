package com.teamside.project.alpha.group.domain.review.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;

public interface ReviewService {
    void createReview(Long groupId, ReviewDto review);
    void updateReview(Long groupId, ReviewDto.UpdateReviewDto review);

    ReviewDto.ResponseReviewDetail selectReviewDetail(Long groupId, String reviewId);

    void createComment(Long groupId, CommentDto.CreateComment comment, String reviewId);

    void keepReview(Long groupId, String reviewId);

    void deleteReview(Long groupId, String reviewId) throws CustomException;

    void updateComment(Long groupId, CommentDto.CreateComment comment, String reviewId, Long commentId);

    void deleteComment(Long groupId, String reviewId, Long commentId);
}
