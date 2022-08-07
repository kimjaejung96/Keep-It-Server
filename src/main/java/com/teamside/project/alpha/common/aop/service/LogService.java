package com.teamside.project.alpha.common.aop.service;

import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import com.teamside.project.alpha.common.aop.repository.ApiLogRepo;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    private final ApiLogRepo apiLogRepo;

    public LogService(ApiLogRepo apiLogRepo) {
        this.apiLogRepo = apiLogRepo;
    }

    public void insertLog(ApiLogEntity apiLogEntity) {
        apiLogRepo.save(apiLogEntity);
    }
}
