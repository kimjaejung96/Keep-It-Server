package com.teamside.project.alpha.common.exception.handler;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ApiExceptionDto;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ResponseObject> handleCustomException(CustomException ex) {
        ResponseObject responseObject = new ResponseObject(ex.getApiExceptionCode());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    // 사용자 정의 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleException(Exception ex) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.SYSTEM_ERROR);
        log.error("RuntimeExceptionHandler : {} \n StackTrace : ", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.BAD_REQUEST);
        log.error("NotReadable Exception : {} \n StackTrace : ", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
    }

    // @RequestBody, @RequestHeader 유효성 실패.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("파라미터 유효성 체크 실패. : {}", ex.getMessage());
        ResponseObject response = new ResponseObject(ApiExceptionCode.SYSTEM_ERROR);

        if (!ex.getConstraintViolations().isEmpty()) {
            String exceptionMsg = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
            response.setBody(exceptionMsg);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // @RequestBody 유효성 실패.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        log.error("밸리데이션 유효성 체크 실패. : {}", ex.getMessage());
        ResponseObject response = new ResponseObject(ApiExceptionCode.SYSTEM_ERROR);

        if (ex.getBindingResult().hasErrors())
            response.setBody(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
