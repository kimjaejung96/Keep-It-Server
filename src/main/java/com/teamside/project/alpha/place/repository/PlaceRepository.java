package com.teamside.project.alpha.place.repository;

import com.teamside.project.alpha.place.model.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    Optional<PlaceEntity> findByPlaceId(Long id);
    boolean existsByPlaceId(Long id);
}
