package com.teamside.project.alpha.group.domain.daily.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.domain.daily.service.DailyService;
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
    public ResponseEntity<ResponseObject> createDaily(@PathVariable String groupId, @RequestBody @Valid DailyDto dailyDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        dailyService.createDaily(groupId, dailyDto);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PatchMapping
    public ResponseEntity<ResponseObject> updateDaily(@PathVariable String groupId, @RequestBody @Valid DailyDto.UpdateDailyDto dailyDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        dailyService.updateDaily(groupId, dailyDto);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/{dailyId}")
    public ResponseEntity<ResponseObject> selectDaily(@PathVariable String groupId, @PathVariable String dailyId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(dailyService.selectDaily(groupId, dailyId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @DeleteMapping("/{dailyId}")
    public ResponseEntity<ResponseObject> deleteDaily(@PathVariable String groupId, @PathVariable String dailyId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        dailyService.deleteDaily(groupId, dailyId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{dailyId}/comment")
    public ResponseEntity<ResponseObject> createComment(@PathVariable String groupId,
                                                        @PathVariable String dailyId,
                                                        @RequestBody @Valid CommentDto.CreateComment comment) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        responseObject.setBody(dailyService.createComment(groupId, dailyId, comment));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PatchMapping("/{dailyId}/comment/{commentId}")
    public ResponseEntity<ResponseObject> updateComment(@PathVariable String groupId,
                                                        @PathVariable String dailyId,
                                                        @PathVariable Long commentId,
                                                        @RequestBody @Valid CommentDto.CreateComment comment) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        dailyService.updateComment(groupId, dailyId, commentId, comment);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @DeleteMapping("/{dailyId}/comment/{commentId}")
    public ResponseEntity<ResponseObject> deleteComment(@PathVariable String groupId,
                                                        @PathVariable String dailyId,
                                                        @PathVariable Long commentId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        dailyService.deleteComment(groupId, dailyId, commentId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{dailyId}/keep")
    public ResponseEntity<ResponseObject> keepDaily(@PathVariable String groupId, @PathVariable String dailyId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        dailyService.keepDaily(groupId, dailyId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
