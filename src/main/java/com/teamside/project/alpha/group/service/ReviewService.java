package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.group.model.dto.ReviewDto;

public interface ReviewService {
    void createReview(ReviewDto review);
    void updateReview(ReviewDto.UpdateReviewDto review);
}
