package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
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
import java.util.List;
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
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        //회원가입
        JwtTokens jwtTokens = memberService.sigunUp(signUpDto);
        responseObject.setBody(jwtTokens);
        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity<ResponseObject> checkExistName(
             @Pattern(regexp = KeepitConstant.REGEXP_MEMBER_NAME,
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
    @PostMapping("/inquiry")
    public ResponseEntity<ResponseObject> inquiry(@RequestBody @Valid InquiryDto inquiryDto) {
        memberService.inquiry(inquiryDto);
        return new ResponseEntity<>(new ResponseObject(ApiExceptionCode.OK), HttpStatus.CREATED);
    }
    @PostMapping("/search")
    public ResponseEntity<ResponseObject> search(@RequestParam String name,
                                                 @RequestParam(required = false) Long groupId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        List<MemberDto.InviteMemberList> names = memberService.search(name, groupId);
        responseObject.setBody(names);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{targetMid}/follow")
    public ResponseEntity<ResponseObject> follow(@PathVariable String targetMid) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        memberService.follow(targetMid);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
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
