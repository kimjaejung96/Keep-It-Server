package com.teamside.project.alpha.member.domain.auth.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.domain.auth.model.dto.JwtTokens;
import com.teamside.project.alpha.member.domain.auth.model.dto.SmsAuthDto;
import com.teamside.project.alpha.member.domain.auth.model.enumurate.AuthType;

public interface AuthService {
    JwtTokens createTokens(String mid);
    String refreshAccessToken(String refreshToken) throws CustomException;
    String refreshRefreshToken() throws CustomException;
    void tokenValidationCheck(String token) throws CustomException;
    String getAuthPayload(String token) ;
    void saveSmsLog(String requestPhoneNum, String number);
    void checkAuthNum(SmsAuthDto smsAuthDto) throws CustomException;
    JwtTokens checkMember(String phone) throws CustomException;
    void checkPhone(String phone, AuthType authType);

    void updateFcmTokenLife(String mid);
}
