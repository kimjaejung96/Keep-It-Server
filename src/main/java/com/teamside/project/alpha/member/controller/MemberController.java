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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Random;

@Validated
@RestController
@RequestMapping("/members")
public class MemberController {

    private final ApplicationEventPublisher smsEventPublisher;
    private final MemberService memberService;

    public MemberController(ApplicationEventPublisher smsEventPublisher, MemberService memberService) {
        this.smsEventPublisher = smsEventPublisher;
        this.memberService = memberService;
    }



    @PostMapping("/sign-up")
    public ResponseEntity<ResponseObject> signUp(@RequestBody @Valid MemberDto.SignUpDto signUpDto) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        //회원가입
        JwtTokens jwtTokens = memberService.sigunUp(signUpDto);
        responseObject.setBody(jwtTokens);
        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<ResponseObject> checkExistName(
             @Pattern(regexp = "(?=.*[-_A-Za-z0-9])(?=.*[^-_]).{4,20}",
            message = "이름이 올바르지 않습니다.") @PathVariable String name) throws CustomException {
        memberService.checkExistsName(name);
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObject> logout() {
        ResponseObject response = new ResponseObject(ApiExceptionCode.OK);
        memberService.logout();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ResponseObject> withdrawal() throws CustomException {
        ResponseObject response = new ResponseObject(ApiExceptionCode.OK);
        memberService.withdrawal();
        return new ResponseEntity<>(response, HttpStatus.OK);
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
