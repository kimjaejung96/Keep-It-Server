package com.teamside.project.alpha.group.domain.daily.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
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
        private String dailyId;
    }

    @Getter
    public static class ResponseDailyInGroup {
        private final List<DailyInGroup> dailyData;

        private final Long lastDailySeq;
        private final Long dailyCount;

        public ResponseDailyInGroup(List<DailyInGroup> dailyData, Long lastDailySeq, Long dailyCount) {
            this.dailyData = dailyData;
            this.lastDailySeq = lastDailySeq;
            this.dailyCount = dailyCount;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DailyInGroup {
        private String dailyId;

        private Long dailySeq;

        private String title;

        private String image;

        private String name;

        private Integer commentCount;

        private String createDt;

        @QueryProjection
        public DailyInGroup(String dailyId, Long dailySeq, String title, String image, String name, Integer commentCount, String createDt) {
            this.dailyId = dailyId;
            this.dailySeq = dailySeq;
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
        String loginMemberProfileUrl;

        public ResponseDailyDetail(DailyDetail dailyDetail, List<CommentDto> comments, MemberEntity loginMember) {
            this.dailyDetail = dailyDetail;
            this.comments = comments;
            this.loginMemberName = loginMember.getName();
            this.loginMemberProfileUrl = loginMember.getProfileUrl();
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
        private String groupName;
        private Boolean isKeep;

        @QueryProjection
        public DailyDetail(String title, String mid, String memberName, String memberProfileUrl, String createDt, String content, String imageUrl, String groupName, Boolean isKeep) {
            this.title = title;
            this.mid = mid;
            this.memberName = memberName;
            this.memberProfileUrl = memberProfileUrl;
            this.createDt = createDt;
            this.content = content;
            this.imageUrl = imageUrl;
            this.groupName = groupName;
            this.isKeep = isKeep;
        }
    }
}
