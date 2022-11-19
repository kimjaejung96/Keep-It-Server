package com.teamside.project.alpha.notification.service;

import com.teamside.project.alpha.notification.model.dto.NotificationDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> getNotifications(Long pageSize, Long lastSeq);
}
