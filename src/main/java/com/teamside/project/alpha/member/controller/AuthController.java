package com.teamside.project.alpha.member.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.filter.AuthCheck;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.service.AuthService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Random;

@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ApplicationEventPublisher smsEventPublisher;

    public AuthController(AuthService authService, ApplicationEventPublisher smsEventPublisher) {
        this.authService = authService;
        this.smsEventPublisher = smsEventPublisher;
    }

    @PostMapping(value = "/sms/{phone}")
    public ResponseEntity<ResponseObject> sms(
            @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                    message = "핸드폰 번호가 올바르지 않습니다.") @PathVariable String phone) throws CustomException {
        String number = "000000";
//        String number = generateCertificationNumber();

        // authNum publish
//        smsEventPublisher.publishEvent(new SMSEvent(phone, number));

        // save smsLog
        authService.saveSmsLog(phone, number);

        return new ResponseEntity(new ResponseObject(ApiExceptionCode.OK), HttpStatus.OK);
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

    @PostMapping(value = "/refresh/refresh-token")
    public ResponseEntity<ResponseObject> refreshRefreshToken() throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);

        JwtTokens jwtTokens = JwtTokens.builder()
                .refreshToken(authService.refreshRefreshToken())
                .build();

        responseObject.setBody(jwtTokens);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }


    @PostMapping(value = "/refresh/access-token")
    public ResponseEntity<ResponseObject> refreshAccessToken(@RequestHeader(value = AuthCheck.REFRESH_TOKEN, required = false) @Valid @NotBlank(message = "리프레시 토큰이 널이거나 빈값입니다.") String refreshToken) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        authService.tokenValidationCheck(refreshToken);

        JwtTokens jwtTokens = JwtTokens.builder()
                .accessToken(authService.refreshAccessToken(refreshToken))
                .build();
        responseObject.setBody(jwtTokens);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }



    private String generateCertificationNumber() {
        Random random = new Random();
        Integer randomNumber = random.ints(100000, 1000000).findFirst().getAsInt();
        return randomNumber.toString();
    }
}
