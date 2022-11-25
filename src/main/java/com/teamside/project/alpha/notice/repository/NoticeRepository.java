package com.teamside.project.alpha.notice.repository;

import com.teamside.project.alpha.notice.model.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long>, NoticeRepositoryDSL {
}
