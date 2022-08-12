package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.SmsAuthDto;

public interface AuthService {
    JwtTokens getTokens(String mid);
    String createAccessToken(String mid);
    String createRefreshToken(String mid);
    void tokenValidationCheck(String token) throws CustomException;
    String getAuthPayload(String token) ;
    void saveSmsLog(String requestPhoneNum, String number) throws CustomException;
    void checkAuthNum(SmsAuthDto smsAuthDto) throws CustomException;
    JwtTokens checkMember(String phone) throws CustomException;
}
