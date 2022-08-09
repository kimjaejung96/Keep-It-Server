package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.model.dto.MemberDto;

public interface MemberService {
    boolean checkId(String name) throws CustomException;
    String sigunUp(MemberDto.SignUpDto signUpDto);
}
