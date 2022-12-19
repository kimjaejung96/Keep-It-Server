package com.teamside.project.alpha.place.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceDto {
    private Long placeId;
    private String placeName;
    private String address;
    private String roadAddress;
    private String phone;
    private BigDecimal x;
    private BigDecimal y;

    public PlaceDto(Long placeId, String placeName, String address, String roadAddress, String phone, BigDecimal x, BigDecimal y) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.address = address;
        this.roadAddress = roadAddress;
        this.phone = phone;
        this.x = x;
        this.y = y;
    }

    @Getter
    @NoArgsConstructor
    public static class PlacePinDto extends PlaceDto {
        private String imageUrl;
        private Long reviewCount;

        public PlacePinDto(Long placeId, String placeName, String address, String roadAddress, String phone, BigDecimal x, BigDecimal y, Long reviewCount, String imageUrl) {
            super(placeId, placeName, address, roadAddress, phone, x, y);
            if (imageUrl != null && imageUrl.split(",").length >= 2) {
                this.imageUrl = imageUrl.split(",")[0];
            } else {
                this.imageUrl = imageUrl;
            }
            this.reviewCount = reviewCount;
        }
    }

    @Getter
    public static class ReviewsInPlace {
        private final List<PlaceDto.ReviewInfo> reviewData;
        private final Long lastReviewSeq;

        public ReviewsInPlace(List<PlaceDto.ReviewInfo> reviewData, Long pageSize) {
            this.reviewData = reviewData;
            if (reviewData.size() == pageSize) {
                this.lastReviewSeq = reviewData.get(reviewData.size() - 1).getReview().reviewSeq;
            } else {
                this.lastReviewSeq = null;
            }
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReviewInfo {
        private Review review;
        private Member member;
        private Place place;

        @QueryProjection
        public ReviewInfo(Review review, Member member, Place place) {
            this.review = review;
            this.member = member;
            this.place = place;
        }

        @Getter
        public static class Review {
            private final String reviewId;

            private final Long reviewSeq;
            private final String content;
            private final Integer commentCount;
            private final String createDt;
            private final Long keepCount;
            private final Boolean isKeep;
            private final List<String> images;

            @QueryProjection
            public Review(String reviewId, Long reviewSeq, String content, Integer commentCount, LocalDateTime createDt, String images, Long keepCount, Boolean isKeep) {
                this.reviewId = reviewId;
                this.reviewSeq = reviewSeq;
                this.content = content;
                this.commentCount = commentCount;
                this.createDt = String.valueOf(createDt);
                this.images = List.of(images.split(","));
                this.keepCount = keepCount;
                this.isKeep = isKeep;
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
            private String address;

            @QueryProjection
            public Place(Long placeId, String placeName, String roadAddress, String address) {
                this.placeId = placeId;
                this.placeName = placeName;
                this.roadAddress = roadAddress;
                this.address = address;
            }
        }
    }

}
