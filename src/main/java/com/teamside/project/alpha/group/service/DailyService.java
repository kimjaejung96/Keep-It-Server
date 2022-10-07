package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.DailyDto;

public interface DailyService {
    void createDaily(Long groupId, DailyDto dailyDto) throws CustomException;
    void updateDaily(Long groupId, DailyDto.UpdateDailyDto dailyDto) throws CustomException;
    void createComment(Long groupId, Long dailyId, CommentDto.CreateComment comment) throws CustomException;
    void keepDaily(Long groupId, Long dailyId);
    DailyDto.ResponseDailyDetail selectDaily(Long groupId, Long dailyId);
    void deleteDaily(Long groupId, Long dailyId) throws CustomException;
    void updateComment(Long groupId, Long dailyId, Long commentId, CommentDto.CreateComment comment);
    void deleteComment(Long groupId, Long dailyId, Long commentId);
}
