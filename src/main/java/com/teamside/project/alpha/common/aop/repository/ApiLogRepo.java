package com.teamside.project.alpha.common.aop.repository;

import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiLogRepo extends JpaRepository<ApiLogEntity, Long> {

}
