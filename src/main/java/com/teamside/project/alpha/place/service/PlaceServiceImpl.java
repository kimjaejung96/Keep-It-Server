package com.teamside.project.alpha.place.service;

import com.teamside.project.alpha.place.model.dto.PlaceDto;
import com.teamside.project.alpha.place.model.entity.PlaceEntity;
import com.teamside.project.alpha.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public void createPlace(PlaceDto place) {
        if (placeRepository.findByPlaceId(place.getPlaceId()).isPresent()) {
            return;
        }

        PlaceEntity newPlace = new PlaceEntity(place);
        placeRepository.save(newPlace);

    }
}
