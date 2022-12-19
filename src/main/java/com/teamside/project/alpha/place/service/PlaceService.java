package com.teamside.project.alpha.place.service;

import com.teamside.project.alpha.place.model.dto.PlaceDto;

import java.util.List;

public interface PlaceService {
    void createPlace(PlaceDto place);

    List<PlaceDto.PlacePinDto> getPlacePins(String groupId);

    PlaceDto.ReviewsInPlace getPlaceReviews(Long placeId, String groupId, Long pageSize, Long lastReviewSeq);
}
