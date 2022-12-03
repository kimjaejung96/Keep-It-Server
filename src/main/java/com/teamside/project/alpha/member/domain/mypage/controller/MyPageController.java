package com.teamside.project.alpha.member.domain.mypage.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyKeep;
import com.teamside.project.alpha.member.domain.mypage.model.enumurate.MyGroupManagementType;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @GetMapping("/groups/management")
    public ResponseEntity<ResponseObject> getMyGroupsManagements(@RequestParam MyGroupManagementType type) {
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
    public ResponseEntity<ResponseObject> getKeepMyReviews(@RequestParam(required = false) Long nextOffset,
                                                       @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getKeepMyReviews(nextOffset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping("/keep/daily")
    public ResponseEntity<ResponseObject> getKeepMyDaily(@RequestParam(required = false) Long nextOffset,
                                                           @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getKeepMyDaily(nextOffset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @PatchMapping("/keep/edit")
    public ResponseEntity<ResponseObject> editKeep(@RequestBody MyKeep.editKeep editKeep) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        myPageService.editKeep(editKeep);

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
    @GetMapping("/following")
    public ResponseEntity<ResponseObject> getMyFollowingList(@RequestParam(required = false) Long nextOffset, @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyFollowingList(nextOffset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

//    @GetMapping("/groups")
//    public ResponseEntity<ResponseObject> getGroups(@RequestParam String type) {
//        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
//        return new ResponseEntity(responseObject, HttpStatus.OK);
//    }

    @GetMapping("/blocks")
    public ResponseEntity<ResponseObject> getMyBlocks(@RequestParam(required = false) Long nextOffset, @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(myPageService.getMyBlocks(nextOffset, pageSize));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @PostMapping("/inquiry")
    public ResponseEntity<ResponseObject> myInquiry(@RequestBody @Valid InquiryDto.MyInquiry myInquiry) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        myPageService.myInquiry(myInquiry);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }


}
