package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.domain.auth.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.AlarmDto;
import com.teamside.project.alpha.member.model.dto.InquiryDto;
import com.teamside.project.alpha.member.model.dto.MemberDto;

import java.util.List;

public interface MemberService {
    JwtTokens sigunUp(MemberDto.SignUpDto signUpDto) throws CustomException;
    void logout();
    void withdrawal() throws CustomException;
    void checkExistsName(String name) throws CustomException;
    void inquiry(InquiryDto inquiryDto);
    List<MemberDto.InviteMemberList> search(String name, String groupId);
    void block(String targetMid) throws CustomException;

    void updateFcm(String fcmToken);
    void updateAlarm(AlarmDto alarmDto);
    AlarmDto selectAlarm();
    void updateTerms(AlarmDto alarmDto);
}
