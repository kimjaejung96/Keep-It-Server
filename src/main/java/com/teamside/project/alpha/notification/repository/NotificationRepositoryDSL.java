package com.teamside.project.alpha.notification.repository;

import com.teamside.project.alpha.notification.model.entity.NotificationEntity;

import java.util.List;

public interface NotificationRepositoryDSL {
    List<NotificationEntity> getNotifications();
}
