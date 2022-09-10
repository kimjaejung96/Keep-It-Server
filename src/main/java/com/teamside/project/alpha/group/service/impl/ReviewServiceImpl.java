package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.group.domain.ReviewEntity;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final GroupRepository groupRepository;

    public ReviewServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public void createReview(ReviewDto.CreateReviewDto review) {
        GroupEntity group = groupRepository.findByGroupId(review.getGroupId()).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        ReviewEntity reviewEntity = new ReviewEntity(review);
        group.createReview(reviewEntity);
    }
}
