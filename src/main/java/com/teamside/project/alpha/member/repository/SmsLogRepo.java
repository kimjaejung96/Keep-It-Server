package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.SmsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SmsLogRepo extends JpaRepository<SmsLogEntity, Long> {
    Optional<SmsLogEntity> findTop1ByPhoneAndCreateTimeBetweenOrderByCreateTimeDesc(String phone, LocalDateTime startTime, LocalDateTime endTime);
}
