package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.domain.auth.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.AlarmDto;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.service.MemberService;
import com.teamside.project.alpha.sms.event.SMSEvent;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final ApplicationEventPublisher smsEventPublisher;
    private final MemberService memberService;


    /**
     * 회원가입 API
     * @param signUpDto
     * @return
     * @throws CustomException
     */
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
        if (name.contains("\\.\\.") || name.contains("\\_\\_")) {
            throw new CustomException(ApiExceptionCode.VALIDATION_ERROR);
        }
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
                                                 @RequestParam(required = false) String groupId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        List<MemberDto.InviteMemberList> names = memberService.search(name, groupId);
        responseObject.setBody(names);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }


    @PostMapping("/{targetMid}/block")
    public ResponseEntity<ResponseObject> block(@PathVariable String targetMid, @RequestParam(required = false) String groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        memberService.block(targetMid, groupId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PutMapping("/fcm")
    public ResponseEntity<ResponseObject> updateFcmToken(@RequestParam String fcmToken) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        memberService.updateFcm(fcmToken);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/alarms")
    public ResponseEntity<ResponseObject> selectAlarm() {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(memberService.selectAlarm());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PatchMapping("/alarms")
    public ResponseEntity<ResponseObject> updateAlarmSetting(@RequestBody AlarmDto alarmDto) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        memberService.updateAlarm(alarmDto);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PatchMapping("/terms")
    public ResponseEntity<ResponseObject> updateTerms(@RequestBody AlarmDto alarmDto) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        memberService.updateTerms(alarmDto);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<ResponseObject> updateMember(@RequestBody MemberDto.UpdateMember updateMember) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        memberService.updateMember(updateMember);
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
        Integer randomNumber = random.ints(100000, 1000000).findAny().getAsInt();
        return randomNumber.toString();
    }

}
