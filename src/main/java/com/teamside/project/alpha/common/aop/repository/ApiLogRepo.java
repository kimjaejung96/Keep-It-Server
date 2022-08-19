package com.teamside.project.alpha.common.aop.repository;

import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepo extends JpaRepository<ApiLogEntity, Long> {

}
