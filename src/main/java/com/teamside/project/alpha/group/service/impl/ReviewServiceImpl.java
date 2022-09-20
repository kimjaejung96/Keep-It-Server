package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.group.model.domain.ReviewEntity;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.ReviewService;
import com.teamside.project.alpha.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final GroupRepository groupRepository;
    private final PlaceRepository placeRepository;

    public ReviewServiceImpl(GroupRepository groupRepository, PlaceRepository placeRepository) {
        this.groupRepository = groupRepository;
        this.placeRepository = placeRepository;
    }

    @Override
    @Transactional
    public void createReview(ReviewDto.CreateReviewDto review) {
        placeRepository.findByPlaceId(review.getPlaceId()).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.PLACE_NOT_EXIST));
        GroupEntity group = groupRepository.findByGroupId(review.getGroupId()).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistReview(review.getPlaceId());

        ReviewEntity reviewEntity = new ReviewEntity(review);
        group.createReview(reviewEntity);
    }
}
