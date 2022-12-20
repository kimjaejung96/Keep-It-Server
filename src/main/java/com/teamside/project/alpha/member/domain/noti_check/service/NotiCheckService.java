package com.teamside.project.alpha.member.domain.noti_check.service;

import com.teamside.project.alpha.common.exception.CustomException;

public interface NotiCheckService {
    Boolean notiCheck() throws CustomException;
}
