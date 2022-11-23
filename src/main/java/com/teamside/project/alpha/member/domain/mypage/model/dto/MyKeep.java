package com.teamside.project.alpha.member.domain.mypage.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyKeep {
    private List<KeepReview> keepReviews;

    private List<KeepDaily> keepDaily;

    @Getter
    @NoArgsConstructor
    public static class KeepReview {
        private Long seq;
        private String placeName;
        private String reviewId;
        private String groupId;
        private String groupName;
        private String memberName;
        private String createDt;
        private String imageUrl;
        private Boolean isDelete;

        public void splitImages(String imageUrl) {
            if (imageUrl != null) {
                this.imageUrl = imageUrl.split(",")[0];
            }
        }
    }

    @Getter
    @NoArgsConstructor
    public static class KeepDaily {
        private Long seq;
        private String dailyId;
        private String title;
        private String imageUrl;
        private String createDt;
        private Boolean isDelete;
        private String groupId;
        private String groupName;
        private String memberName;
        private Long commentCount;
    }

    @Getter
    @NoArgsConstructor
    public static class editKeep {
        private List<Long> keepSeqList;
        private String type;
        private final Boolean isAll = false;
    }

    public MyKeep(List<KeepReview> keepReviews, List<KeepDaily> keepDaily) {
        this.keepReviews = keepReviews;
        this.keepDaily = keepDaily;
    }
}
