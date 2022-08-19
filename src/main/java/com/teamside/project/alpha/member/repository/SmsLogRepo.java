package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.SmsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsLogRepo extends JpaRepository<SmsLogEntity, Long> {
    Optional<SmsLogEntity> findTop1ByPhoneAndCreateTimeBetweenOrderByCreateTimeDesc(String phone, LocalDateTime startTime, LocalDateTime endTime);
}
