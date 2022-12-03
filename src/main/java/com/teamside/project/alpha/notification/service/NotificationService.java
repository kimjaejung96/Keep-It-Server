package com.teamside.project.alpha.notification.service;

import com.teamside.project.alpha.notification.model.dto.NotificationDto;

public interface NotificationService {

    NotificationDto.MyNotifications getNotifications(Long pageSize, Long nextOffset);
}
