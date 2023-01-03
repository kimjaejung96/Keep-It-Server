package com.teamside.project.alpha.member.domain.noti_check.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.domain.noti_check.model.entity.dto.NotificationCheckDTO;

public interface NotiCheckService {
    NotificationCheckDTO notiCheck() throws CustomException;
}
