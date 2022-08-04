package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.sms.event.SMSEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/member")
public class Member {

    private final ApplicationEventPublisher smsEventPublisher;

    @Autowired
    public Member(ApplicationEventPublisher smsEventPublisher) {
        this.smsEventPublisher = smsEventPublisher;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.status(HttpStatus.OK).body("pong!~");
    }


    @PostMapping("/sms")
    public ResponseEntity<String> smsTest(@RequestParam(value = "phone") String receiver ){
        String number = generateCertificationNumber();

        smsEventPublisher.publishEvent(new SMSEvent(receiver, number));

        return ResponseEntity.status(HttpStatus.OK).body("200");
    }



    private String generateCertificationNumber() {
        Random random = new Random();
        Integer randomNumber = random.ints(100000, 1000000)
                .findFirst()
                .getAsInt();
        return randomNumber.toString();
    }
}
