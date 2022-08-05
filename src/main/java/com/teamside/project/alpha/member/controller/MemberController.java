package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.sms.event.SMSEvent;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public MemberController(ApplicationEventPublisher smsEventPublisher) {
        this.smsEventPublisher = smsEventPublisher;
    }


    @PostMapping("/sign-up")
    public ResponseEntity<ResponseObject> signUp(@RequestBody @Validated MemberDto.SignUpDto member) {
        ResponseObject responseObject = new ResponseObject();


        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }


    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.status(HttpStatus.OK).body("pong!~");
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
