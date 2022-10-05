package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyDto {
    @Size(min = 1, max = 50)
    private String title;
    @Size(min = 1, max = 2000)
    private String content;
    private String image;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDailyDto extends DailyDto {
        private Long dailyId;
    }

    @Getter
    public static class ResponseDailyInGroup {
        private final List<DailyInGroup> dailyData;

        private final Long lastDailyId;

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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDailyDetail {
        private DailyDetail dailyDetail;
        List<CommentDto> comments;
        String loginMemberName;

        public ResponseDailyDetail(DailyDetail dailyDetail, List<CommentDto> comments, String loginMemberName) {
            this.dailyDetail = dailyDetail;
            this.comments = comments;
            this.loginMemberName = loginMemberName;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DailyDetail {
        private String title;

        private String mid;
        private String memberName;
        private String memberProfileUrl;
        private String createDt;
        private String content;
        private String imageUrl;
        private Boolean isKeep;

        @QueryProjection
        public DailyDetail(String title, String mid, String memberName, String memberProfileUrl, String createDt, String content, String imageUrl, Boolean isKeep) {
            this.title = title;
            this.mid = mid;
            this.memberName = memberName;
            this.memberProfileUrl = memberProfileUrl;
            this.createDt = createDt;
            this.content = content;
            this.imageUrl = imageUrl;
            this.isKeep = isKeep;
        }
    }
}
