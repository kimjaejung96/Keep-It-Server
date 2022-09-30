package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;

public interface ReviewService {
    void createReview(Long groupId, ReviewDto review);
    void updateReview(Long groupId, ReviewDto.UpdateReviewDto review);

    ReviewDto.ResponseReviewDetail selectReviewDetail(Long groupId, Long reviewId);

    void createComment(Long groupId, CommentDto.CreateComment comment, Long reviewId);

    void keepReview(Long groupId, Long reviewId);
}
