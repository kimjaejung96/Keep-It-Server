package com.teamside.project.alpha.notification.service.impl;

import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
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
    public List<NotificationDto> getNotifications(Long pageSize, Long lastSeq) {
        List<NotificationDto> entityList = notificationRepository.getNotifications(pageSize, lastSeq);
        entityList.forEach(data -> data.createMsg());
        return entityList;
    }

}
