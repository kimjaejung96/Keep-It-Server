package com.teamside.project.alpha.common.aop.api_log.repository;

import com.teamside.project.alpha.common.aop.api_log.model.entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepo extends JpaRepository<ApiLogEntity, Long> {

}
