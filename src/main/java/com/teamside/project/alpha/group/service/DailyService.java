package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.DailyDto;

public interface DailyService {
    void createDaily(DailyDto dailyDto) throws CustomException;
    void updateDaily(DailyDto.UpdateDailyDto dailyDto) throws CustomException;
}
