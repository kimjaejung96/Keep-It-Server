package com.teamside.project.alpha.common.aop.service;

import com.teamside.project.alpha.common.aop.model.entity.ApiLogEntity;
import com.teamside.project.alpha.common.aop.repository.ApiLogRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {
    private static final int DESCRIPTION_MAX_LENGTH = 5000;
    private final ApiLogRepo apiLogRepo;

    public LogService(ApiLogRepo apiLogRepo) {
        this.apiLogRepo = apiLogRepo;
    }

    public void insertLog(ApiLogEntity apiLogEntity) {
        if (apiLogEntity.getDescription().length() <= 5000) {
            apiLogRepo.save(apiLogEntity);
        } else {
            List<ApiLogEntity> apiLogs = new ArrayList<>();
            apiLogs.add(new ApiLogEntity(apiLogEntity.getMid(),
                    apiLogEntity.getName(),
                    apiLogEntity.getDescription().substring(0, 5000),
                    apiLogEntity.getStatus(),
                    apiLogEntity.getProcessTime(),
                    apiLogEntity.getCode()
                    ));


            String desc = apiLogEntity.getDescription();
            int textLen = apiLogEntity.getDescription().length();
            int loopCnt = textLen / DESCRIPTION_MAX_LENGTH + 1;
            String splitedDesc;

            for (int i = 1; i < loopCnt; i++) {
                int lastIndex = (i + 1) * DESCRIPTION_MAX_LENGTH;

                if (textLen > lastIndex) {
                    splitedDesc = desc.substring(i*DESCRIPTION_MAX_LENGTH, lastIndex);
                } else {
                    splitedDesc = desc.substring(i*DESCRIPTION_MAX_LENGTH);
                }
                apiLogs.add(new ApiLogEntity("", "", splitedDesc, "", 0, 0));
            }


            apiLogRepo.saveAll(apiLogs);
        }
    }
}
