package com.teamside.project.alpha.notification.repository;

import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.model.entity.NotificationEntity;

import java.util.List;

public interface NotificationRepositoryDSL {
    List<NotificationDto> getNotifications(Long pageSize, Long lastOffset);
}
