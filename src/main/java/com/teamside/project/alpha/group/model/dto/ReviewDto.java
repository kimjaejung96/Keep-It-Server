package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.domain.ReviewEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.place.model.entity.PlaceEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDto {
    @NotNull
    private long placeId;
    @Size(min = 15, max = 2000)
    private String content;
    private List<String> images;

    @Getter
    public static class UpdateReviewDto extends ReviewDto{
        private long reviewId;
    }

    @Getter
    public static class ResponseSelectReviewsInGroup {
        private final List<SelectReviewsInGroup> reviewData;
        private final Long lastReviewId;

        public ResponseSelectReviewsInGroup(List<SelectReviewsInGroup> reviewData, Long lastReviewId) {
            this.reviewData = reviewData;
            this.lastReviewId = lastReviewId;
        }
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SelectReviewsInGroup {
        private Review review;
        private Member member;
        private Place place;

        @QueryProjection
        public SelectReviewsInGroup(Review review, Member member, Place place) {
            this.review = review;
            this.member = member;
            this.place = place;
        }
        @Getter
        public static class Review {
            private final Long reviewId;
            private final String content;
            private final Integer commentCount;
            private final String createDt;
            private final List<String> images;

            @QueryProjection
            public Review(Long reviewId, String content, Integer commentCount, LocalDateTime createDt, String images) {
                this.reviewId = reviewId;
                this.content = content;
                this.commentCount = commentCount;
                this.createDt = String.valueOf(createDt);
                this.images = List.of(images.split(","));
            }
        }
        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Member {
            private String mid;
            private String name;
            private String profileUrl;

            @QueryProjection
            public Member(String mid, String name, String profileUrl) {
                this.mid = mid;
                this.name = name;
                this.profileUrl = profileUrl;
            }
        }
        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Place {
            private Long placeId;
            private String placeName;
            private String roadAddress;
            @QueryProjection
            public Place(Long placeId, String placeName, String roadAddress) {
                this.placeId = placeId;
                this.placeName = placeName;
                this.roadAddress = roadAddress;
            }
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseReviewDetail {
        ReviewDetail reviewsDetail;
        List<CommentDto> comments;
        String loginMemberName;

        public ResponseReviewDetail(ReviewDetail reviewsDetail, List<CommentDto> comments, String loginMemberName) {
            this.reviewsDetail = reviewsDetail;
            this.comments = comments;
            this.loginMemberName = loginMemberName;
        }
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReviewDetail {
        private String reviewContent;
        private String memberName;
        private String memberProfileUrl;
        private String placeName;
        private String placeAddress;
        private List<String> reviewImagesUrl;
        private String reviewCreateDt;
        private int keepCount;
        private Boolean isKeep;

        @QueryProjection
        public ReviewDetail(ReviewEntity review, MemberEntity member, PlaceEntity place) {
            this.reviewContent = review.getContent();
            this.memberName = member.getName();
            this.memberProfileUrl = member.getProfileUrl();
            this.placeName = place.getPlaceName();
            this.placeAddress = place.getAddress();
            this.reviewImagesUrl = List.of(review.getImages().split(","));
            this.reviewCreateDt = String.valueOf(review.getCreateTime());
            this.keepCount = review.getReviewKeepEntities().size();
            this.isKeep = review.getReviewKeepEntities().stream().anyMatch(r -> r.getMember().getMid().equals(CryptUtils.getMid()));
        }
    }

}
