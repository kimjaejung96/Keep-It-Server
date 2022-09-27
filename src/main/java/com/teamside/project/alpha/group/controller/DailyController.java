package com.teamside.project.alpha.group.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.service.DailyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily")
public class DailyController {
    private final DailyService dailyService;

    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createDaily(@RequestBody DailyDto dailyDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        dailyService.createDaily(dailyDto);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
    @PatchMapping
    public ResponseEntity<ResponseObject> updateDaily(@RequestBody DailyDto.UpdateDailyDto dailyDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        dailyService.updateDaily(dailyDto);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
}
