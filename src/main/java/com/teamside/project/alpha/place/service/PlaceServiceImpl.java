package com.teamside.project.alpha.place.service;

import com.teamside.project.alpha.place.model.dto.PlaceDto;
import com.teamside.project.alpha.place.model.entity.PlaceEntity;
import com.teamside.project.alpha.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    @Transactional
    public void createPlace(PlaceDto place) {
        Optional<PlaceEntity> placeEntity = placeRepository.findByPlaceId(place.getPlaceId());
        if (placeEntity.isEmpty()){
            PlaceEntity newPlace = new PlaceEntity(place);
            placeRepository.save(newPlace);
        } else {
            placeEntity.get().placeCategoryCheck(place);
        }
    }

    @Override
    public List<PlaceDto.PlacePinDto> getPlacePins(String groupId) {
        return placeRepository.getPlacePins(groupId);
    }

    @Override
    public PlaceDto.ReviewsInPlace getPlaceReviews(Long placeId, String groupId, Long pageSize, Long lastReviewSeq) {
        List<PlaceDto.ReviewInfo> data = placeRepository.getPlaceReviews(placeId, groupId, pageSize, lastReviewSeq);
        return new PlaceDto.ReviewsInPlace(data, pageSize);
    }

}
