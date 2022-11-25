package com.teamside.project.alpha.notice.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.notice.service.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> notices(@RequestParam Long pageSize,
                                                  @RequestParam(required = false) Long lastNoticeId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(noticeService.getNotices(pageSize, lastNoticeId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ResponseObject> notice(@PathVariable Long noticeId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(noticeService.getNotice(noticeId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

}
