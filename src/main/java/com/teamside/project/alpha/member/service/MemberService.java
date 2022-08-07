package com.teamside.project.alpha.member.service;

import com.teamside.project.alpha.common.exception.CustomException;

public interface MemberService {
    boolean checkId(String name) throws CustomException;

}
