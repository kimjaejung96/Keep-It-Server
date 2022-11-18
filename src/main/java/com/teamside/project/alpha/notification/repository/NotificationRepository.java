package com.teamside.project.alpha.notification.repository;

import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findAllByReceiverMidAndNotiDateGreaterThan(String mid, LocalDateTime localDateTime);
}
