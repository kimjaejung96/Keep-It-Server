package com.teamside.project.alpha.notification.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.notification.model.dto.NotificationDto;

public interface NotificationService {

    NotificationDto.MyNotifications getNotifications(String type, Long pageSize, Long nextOffset) throws CustomException;
}
