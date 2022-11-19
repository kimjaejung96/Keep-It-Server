package com.teamside.project.alpha.notification.repository;

import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, NotificationRepositoryDSL {
}
