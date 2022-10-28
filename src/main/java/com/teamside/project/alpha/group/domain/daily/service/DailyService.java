package com.teamside.project.alpha.group.domain.daily.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;

public interface DailyService {
    void createDaily(String groupId, DailyDto dailyDto) throws CustomException;
    void updateDaily(String groupId, DailyDto.UpdateDailyDto dailyDto) throws CustomException;
    String createComment(String groupId, String dailyId, CommentDto.CreateComment comment) throws CustomException;
    void keepDaily(String groupId, String dailyId);
    DailyDto.ResponseDailyDetail selectDaily(String groupId, String dailyId);
    void deleteDaily(String groupId, String dailyId) throws CustomException;
    void updateComment(String groupId, String dailyId, String commentId, CommentDto.CreateComment comment);
    void deleteComment(String groupId, String dailyId, String commentId);
}
