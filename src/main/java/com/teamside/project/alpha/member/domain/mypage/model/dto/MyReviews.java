package com.teamside.project.alpha.member.domain.mypage.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyReviews {
    private List<Reviews> myReviews;
    private Long lastSeq;
    @Getter
    @NoArgsConstructor
    public static class Reviews {
        private Long seq;
        private String placeName;
        private String groupName;
        private String createDt;
        private String reviewId;
        private Long commentCount;
        private Long keepCount;
        private String imageUrl;

        public void splitImages(String imageUrl) {
            if (imageUrl != null) {
                this.imageUrl = imageUrl.split(",")[0];
            }
        }
    }

    public MyReviews(List<Reviews> myReviews, Long pageSize) {
        this.myReviews = myReviews;
        if (myReviews.size() == pageSize) {
            this.lastSeq = myReviews.get(pageSize.intValue()-1).getSeq();
        } else {
            this.lastSeq = null;
        }
    }
}
