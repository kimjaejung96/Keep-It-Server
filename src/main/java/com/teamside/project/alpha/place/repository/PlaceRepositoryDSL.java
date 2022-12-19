package com.teamside.project.alpha.place.repository;

import com.teamside.project.alpha.place.model.dto.PlaceDto;

import java.util.List;

public interface PlaceRepositoryDSL {
    List<PlaceDto.PlacePinDto> getPlacePins(String groupId);

    List<PlaceDto.ReviewInfo> getPlaceReviews(Long placeId, String groupId, Long pageSize, Long lastReviewSeq);
}
