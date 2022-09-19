package com.teamside.project.alpha.group.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.PlaceDto;
import com.teamside.project.alpha.group.service.impl.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createPlace(@RequestBody PlaceDto place) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        HttpStatus result = placeService.createPlace(place);
        return new ResponseEntity(responseObject, result);
    }

}
