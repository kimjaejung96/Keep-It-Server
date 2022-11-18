package com.teamside.project.alpha.notification.service;

import com.teamside.project.alpha.notification.model.entity.NotificationEntity;

import java.util.List;

public interface NotificationService {

    List<NotificationEntity> getNotifications();
}
