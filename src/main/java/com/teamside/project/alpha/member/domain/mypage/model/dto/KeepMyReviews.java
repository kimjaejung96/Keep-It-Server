package com.teamside.project.alpha.member.domain.mypage.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KeepMyReviews {
    private List<KeepMyReview> keepMyReviews;
    private Long lastSeq;

    @Getter
    @NoArgsConstructor
    public static class KeepMyReview {
        private Long seq;
        private String placeName;
        private String reviewId;
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

    public KeepMyReviews(List<KeepMyReview> keepMyReviews, Long pageSize) {
        this.keepMyReviews = keepMyReviews;
        if (keepMyReviews.size() == pageSize) {
            this.lastSeq = keepMyReviews.get(pageSize.intValue()-1).getSeq();
        } else {
            this.lastSeq = null;
        }

    }

}
