package com.teamside.project.alpha.common;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {
    @GetMapping("/ping")
    public ResponseEntity<ResponseObject> ping() {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody("PONG~");
        return new ResponseEntity<>(responseObject, HttpStatus.ACCEPTED);
    }

    @GetMapping("/ping/with-token")
    public ResponseEntity<ResponseObject> pingWithToken() {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody("PONG~");
        return new ResponseEntity<>(responseObject, HttpStatus.ACCEPTED);
    }

}
