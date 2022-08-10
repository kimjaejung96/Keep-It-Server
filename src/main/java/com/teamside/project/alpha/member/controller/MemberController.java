package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.service.MemberService;
import com.teamside.project.alpha.sms.event.SMSEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Random;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final ApplicationEventPublisher smsEventPublisher;
    private final MemberService memberService;

    public MemberController(ApplicationEventPublisher smsEventPublisher, MemberService memberService) {
        this.smsEventPublisher = smsEventPublisher;
        this.memberService = memberService;
    }



    @PostMapping("/sign-up")
    public ResponseEntity<ResponseObject> signUp(@RequestBody @Valid MemberDto.SignUpDto signUpDto) throws CustomException {
        if(memberService.existName(signUpDto.getMember().getName())) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
        if (memberService.existPhone(signUpDto.getMember().getPhone())) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_PHONE);
        }
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        //회원가입
        JwtTokens jwtTokens = memberService.sigunUp(signUpDto);
        responseObject.setBody(jwtTokens);
        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }


    @GetMapping("/ping")
    public ResponseEntity<ResponseObject> ping() {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
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
