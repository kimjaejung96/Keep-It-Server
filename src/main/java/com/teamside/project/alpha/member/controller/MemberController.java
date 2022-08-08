package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.service.AuthService;
import com.teamside.project.alpha.member.service.MemberService;
import com.teamside.project.alpha.sms.event.SMSEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final ApplicationEventPublisher smsEventPublisher;
    private final MemberService memberService;
    private final AuthService authService;

    public MemberController(ApplicationEventPublisher smsEventPublisher, MemberService memberService, AuthService authService) {
        this.smsEventPublisher = smsEventPublisher;
        this.memberService = memberService;
        this.authService = authService;
    }



    @PostMapping("/sign-up")
    public ResponseEntity<ResponseObject> signUp(@RequestBody @Validated MemberDto.SignUpDto signUpDto) throws CustomException {
        if(memberService.checkId(signUpDto.getMember().getName())) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
        ResponseObject responseObject = new ResponseObject();
        //회원가입
        String accessToken = authService.createTokens("tests");
        //jwt 생성, refresh 저장
        //jwt 반환 (암호화 여부 확인)
        responseObject.setBody(accessToken);
        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }


    @GetMapping("/ping")
    public ResponseEntity<ResponseObject> ping() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setBody("PONG~");
        return new ResponseEntity<>(responseObject, HttpStatus.ACCEPTED);
    }


    @PostMapping("/sms")
    public ResponseEntity<String> smsTest(@RequestParam(value = "phone") String receiver) {
        String number = generateCertificationNumber();

        smsEventPublisher.publishEvent(new SMSEvent(receiver, number));

        return ResponseEntity.status(HttpStatus.OK).body("200");
    }


    private String generateCertificationNumber() {
        Random random = new Random();
        Integer randomNumber = random.ints(100000, 1000000).findFirst().getAsInt();
        return randomNumber.toString();
    }

}
