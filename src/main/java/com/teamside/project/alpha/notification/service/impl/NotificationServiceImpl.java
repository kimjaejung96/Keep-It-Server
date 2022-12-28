package com.teamside.project.alpha.notification.service.impl;

import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.repository.NotificationRepository;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    @Override
    public NotificationDto.MyNotifications getNotifications(String type, Long pageSize, Long nextOffset) {
        List<NotificationDto> notifications;
        // ACT(활동), NEWS(소식)
        if (type.equals("ACT")) {
            notifications = notificationRepository.getNotifications(pageSize, nextOffset);
            notifications.forEach(data -> data.createMsg());
        } else {
            notifications = notificationRepository.getActNotifications(pageSize, nextOffset);
        }

        return new NotificationDto.MyNotifications(notifications, nextOffset, pageSize);
    }

}
