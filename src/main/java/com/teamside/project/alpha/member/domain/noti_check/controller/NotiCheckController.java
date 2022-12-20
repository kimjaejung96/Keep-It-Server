package com.teamside.project.alpha.member.domain.noti_check.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.domain.noti_check.service.NotiCheckService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/noti")
public class NotiCheckController {
    private final NotiCheckService notiCheckService;
    @GetMapping("/check")
    public ResponseEntity<ResponseObject> notiCheck() throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(notiCheckService.notiCheck());
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
}
