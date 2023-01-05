package com.teamside.project.alpha.notification.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{type}")
    public ResponseEntity<ResponseObject> getNotifications(@PathVariable String type,
                                                           @RequestParam Long pageSize,
                                                           @RequestParam(required = false) Long nextOffset) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(notificationService.getNotifications(type, pageSize, nextOffset));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
