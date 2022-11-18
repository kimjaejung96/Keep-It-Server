package com.teamside.project.alpha.notification.service.impl;

import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
import com.teamside.project.alpha.notification.repository.NotificationRepository;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    @Override
    public List<NotificationEntity> getNotifications() {
        return notificationRepository.findAllByReceiverMidAndNotiDateAfter(CryptUtils.getMid(), LocalDateTime.now().minusWeeks(2));
    }
}
