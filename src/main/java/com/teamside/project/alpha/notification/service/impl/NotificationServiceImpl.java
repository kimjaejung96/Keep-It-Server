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
    public NotificationDto.MyNotifications getNotifications(Long pageSize, Long lastOffset) {
        List<NotificationDto> entityList = notificationRepository.getNotifications(pageSize, lastOffset);
        entityList.forEach(data -> data.createMsg());

        return new NotificationDto.MyNotifications(entityList, lastOffset, pageSize);
    }

}
