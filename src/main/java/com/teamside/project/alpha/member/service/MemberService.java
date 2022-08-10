package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.model.dto.JwtTokens;
import com.teamside.project.alpha.member.model.dto.MemberDto;

public interface MemberService {
    boolean existName(String name) throws CustomException;
    boolean existPhone(String phone) throws CustomException;
    JwtTokens sigunUp(MemberDto.SignUpDto signUpDto);
}
