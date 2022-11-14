package com.teamside.project.alpha.member.domain.mypage.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
