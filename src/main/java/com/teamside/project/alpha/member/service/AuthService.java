package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.SmsAuthDto;

public interface AuthService {
    JwtTokens createTokens(String mid);
    String refreshAccessToken(String refreshToken) throws CustomException;
    String refreshRefreshToken() throws CustomException;
    void tokenValidationCheck(String token) throws CustomException;
    String getAuthPayload(String token) ;
    void saveSmsLog(String requestPhoneNum, String number) throws CustomException;
    void checkAuthNum(SmsAuthDto smsAuthDto) throws CustomException;
    JwtTokens checkMember(String phone) throws CustomException;
    void checkPhone(String phone, String authType) throws CustomException;
}
