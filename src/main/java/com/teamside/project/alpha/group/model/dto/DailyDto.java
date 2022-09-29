package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
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
    private String image;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDailyDto extends DailyDto {
        private Long dailyId;
    }

    @Getter
    public static class ResponseDailyInGroup {
        private List<DailyInGroup> dailyData;

        private Long lastDailyId;

        public ResponseDailyInGroup(List<DailyInGroup> dailyData, Long lastDailyId) {
            this.dailyData = dailyData;
            this.lastDailyId = lastDailyId;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DailyInGroup {
        private Long dailyId;

        private String title;

        private String image;

        private String name;

        private Integer commentCount;

        private String createDt;

        @QueryProjection
        public DailyInGroup(Long dailyId, String title, String image, String name, Integer commentCount, String createDt) {
            this.dailyId = dailyId;
            this.title = title;
            this.image = image;
            this.name = name;
            this.commentCount = commentCount;
            this.createDt = createDt;
        }
    }
}
