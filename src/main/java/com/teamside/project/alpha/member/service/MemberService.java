package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;

public interface MemberService {
    JwtTokens sigunUp(MemberDto.SignUpDto signUpDto) throws CustomException;
    void checkExistsName(String name) throws CustomException;

}
