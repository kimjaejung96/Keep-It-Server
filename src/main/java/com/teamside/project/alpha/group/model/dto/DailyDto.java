package com.teamside.project.alpha.group.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDailyDto {
        private Long groupId;
        private String title;
        private String content;
        private List<String> images;
    }
}
