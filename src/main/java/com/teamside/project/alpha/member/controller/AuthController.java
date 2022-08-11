package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.member.service.AuthService;
import com.teamside.project.alpha.sms.event.SMSEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ApplicationEventPublisher smsEventPublisher;

    public AuthController(AuthService authService, ApplicationEventPublisher smsEventPublisher) {
        this.authService = authService;
        this.smsEventPublisher = smsEventPublisher;
    }

    @PostMapping(value = "/sms")
    public ResponseEntity<ResponseObject> sms(@RequestBody @Valid SmsAuthDto smsAuthDto) throws CustomException {
        String number = generateCertificationNumber();

        // authNum publish
        smsEventPublisher.publishEvent(new SMSEvent(smsAuthDto.getPhone(), number));

        // save smsLog
        authService.saveSmsLog(smsAuthDto.getPhone(), number);

        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(number);

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @PostMapping(value = "/sms/sign-up")
    public ResponseEntity<ResponseObject> smsSignUp(@RequestBody @Valid SmsAuthDto smsAuthDto) throws CustomException {
        // auth sms
        authService.checkAuthNum(smsAuthDto);

        return new ResponseEntity(new ResponseObject(ApiExceptionCode.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/sms/sign-in")
    public ResponseEntity<ResponseObject> smsSignIn(@RequestBody @Valid SmsAuthDto smsAuthDto) throws CustomException {
        // auth sms
        authService.checkAuthNum(smsAuthDto);

        // jwt
        JwtTokens jwtTokens = authService.checkMember(smsAuthDto.getPhone());

        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(jwtTokens);

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    private String generateCertificationNumber() {
        Random random = new Random();
        Integer randomNumber = random.ints(100000, 1000000).findFirst().getAsInt();
        return randomNumber.toString();
    }
}
