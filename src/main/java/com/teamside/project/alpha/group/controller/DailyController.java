package com.teamside.project.alpha.group.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.service.DailyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/groups/{groupId}/daily")
@Validated
public class DailyController {
    private final DailyService dailyService;

    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createDaily(@PathVariable Long groupId, @RequestBody @Valid DailyDto dailyDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        dailyService.createDaily(groupId, dailyDto);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PatchMapping
    public ResponseEntity<ResponseObject> updateDaily(@PathVariable Long groupId, @RequestBody @Valid DailyDto.UpdateDailyDto dailyDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        dailyService.updateDaily(groupId, dailyDto);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/{dailyId}")
    public ResponseEntity<ResponseObject> selectDaily(@PathVariable Long groupId, @PathVariable Long dailyId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(dailyService.selectDaily(groupId, dailyId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{dailyId}/comment")
    public ResponseEntity<ResponseObject> createComment(@PathVariable Long groupId,
                                                        @PathVariable Long dailyId,
                                                        @RequestBody @Valid CommentDto.CreateComment comment) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        dailyService.createComment(groupId, dailyId, comment);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{dailyId}/keep")
    public ResponseEntity<ResponseObject> keepDaily(@PathVariable Long groupId, @PathVariable Long dailyId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        dailyService.keepDaily(groupId, dailyId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
