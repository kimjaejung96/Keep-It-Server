package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;

public interface ReviewService {
    void createReview(Long groupId, ReviewDto review);
    void updateReview(Long groupId, ReviewDto.UpdateReviewDto review);

    ReviewDto.ResponseReviewDetail selectReviewDetail(Long groupId, Long reviewId);

    void createComment(Long groupId, CommentDto.CreateComment comment, Long reviewId);

    void keepReview(Long groupId, Long reviewId);

    void deleteReview(Long groupId, Long reviewId) throws CustomException;

    void updateComment(Long groupId, CommentDto.CreateComment comment, Long reviewId, Long commentId);

    void deleteComment(Long groupId, Long reviewId, Long commentId);
}
