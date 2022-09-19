package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.group.model.dto.PlaceDto;
import org.springframework.http.HttpStatus;

public interface PlaceService {
    HttpStatus createPlace(PlaceDto place);
}
