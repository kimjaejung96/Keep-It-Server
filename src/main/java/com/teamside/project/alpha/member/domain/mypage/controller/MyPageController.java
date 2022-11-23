package com.teamside.project.alpha.member.domain.mypage.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/mypage")
public class MyPageController {
    private final MyPageService myPageService;
    @GetMapping("/home")
    public ResponseEntity<ResponseObject> getMyPageHome() {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyPageHome());

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
    @GetMapping("/groups")
    public ResponseEntity<ResponseObject> getMyGroups() {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyGroups());

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
    @GetMapping("/reviews")
    public ResponseEntity<ResponseObject> getMyReviews(@RequestParam(required = false) String groupId,
                                                       @RequestParam(required = false) Long lastSeq,
                                                       @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyReviews(groupId, lastSeq, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping("/daily")
    public ResponseEntity<ResponseObject> getMyDaily(@RequestParam(required = false) String groupId,
                                                       @RequestParam(required = false) Long lastSeq,
                                                       @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyDaily(groupId, lastSeq, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<ResponseObject> getMyComments(@RequestParam(required = false) String groupId,
                                                     @RequestParam Long offset,
                                                     @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyComments(groupId, offset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping("/keep/reviews")
    public ResponseEntity<ResponseObject> getKeepMyReivews(@RequestParam Long offset,
                                                       @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getKeepMyReviews(offset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping("/keep/daily")
    public ResponseEntity<ResponseObject> getKeepMyDaily(@RequestParam Long offset,
                                                           @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getKeepMyDaily(offset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
}
