package com.teamside.project.alpha.group.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyDto {
    private Long groupId;
    private String title;
    private String content;
    private List<String> images;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDailyDto extends DailyDto {
        private Long dailyId;
    }
}
