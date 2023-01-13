package com.teamside.project.alpha.member.domain.auth.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.domain.auth.model.dto.JwtTokens;
import com.teamside.project.alpha.member.domain.auth.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.domain.auth.model.enumurate.AuthType;
import com.teamside.project.alpha.member.domain.auth.service.AuthService;
import com.teamside.project.alpha.sms.event.SMSEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
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
    private final Environment env;

    public AuthController(AuthService authService, ApplicationEventPublisher smsEventPublisher, Environment env) {
        this.authService = authService;
        this.smsEventPublisher = smsEventPublisher;
        this.env = env;
    }

    @PostMapping(value = "/sms/{phone}")
    public ResponseEntity<ResponseObject> sms(
            @Pattern(regexp = KeepitConstant.REGEXP_PHONE,
                    message = "핸드폰 번호가 올바르지 않습니다.") @PathVariable String phone, @RequestParam AuthType authType) throws CustomException {
        String number;
        // check phone
        authService.checkPhone(phone, authType);

        String activeYml = env.getActiveProfiles()[0];

        if (activeYml.equals("local") || activeYml.equals("dev")) {
            number = "000000";
        } else {
            number = generateCertificationNumber();

            // authNum publish
            smsEventPublisher.publishEvent(new SMSEvent(phone, number));
        }

        // save smsLog
        authService.saveSmsLog(phone, number);

        return new ResponseEntity(new ResponseObject(ApiExceptionCode.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/sms/sign-up")
    public ResponseEntity<ResponseObject> smsSignUp(@RequestBody @Valid SmsAuthDto smsAuthDto) throws CustomException {
        // auth sms
        authService.checkPhone(smsAuthDto.getPhone(), AuthType.SIGN_UP);

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

        authService.updateFcmTokenLife(CryptUtils.getMid());
        responseObject.setBody(jwtTokens);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }


    @PostMapping(value = "/refresh/access-token")
    public ResponseEntity<ResponseObject> refreshAccessToken(@RequestHeader(value = KeepitConstant.REFRESH_TOKEN, required = false) @Valid @NotBlank(message = "리프레시 토큰이 널이거나 빈값입니다.")  String refreshToken) throws CustomException {
        if (!refreshToken.startsWith("Bearer ")) throw new CustomException(ApiExceptionCode.UNAUTHORIZED);

        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        authService.tokenValidationCheck(refreshToken);

        JwtTokens jwtTokens = JwtTokens.builder()
                .accessToken(authService.refreshAccessToken(refreshToken))
                .build();
        String mid = authService.getAuthPayload(refreshToken);

        authService.updateFcmTokenLife(mid);

        responseObject.setBody(jwtTokens);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }



    private String generateCertificationNumber() {
        Random random = new Random();
        Integer randomNumber = random.ints(100000, 1000000).findAny().getAsInt();
        return randomNumber.toString();
    }
}
