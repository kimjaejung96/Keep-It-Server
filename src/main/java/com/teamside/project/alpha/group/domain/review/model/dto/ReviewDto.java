package com.teamside.project.alpha.group.domain.review.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewKeepEntity;
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
        private String reviewId;
    }

    @Getter
    public static class ResponseSelectReviewsInGroup {
        private final List<SelectReviewsInGroup> reviewData;
        private final Long lastReviewSeq;
        private final Long reviewCount;

        public ResponseSelectReviewsInGroup(List<SelectReviewsInGroup> reviewData, Long lastReviewSeq, Long reviewCount) {
            this.reviewData = reviewData;
            this.lastReviewSeq = lastReviewSeq;
            this.reviewCount = reviewCount;
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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseReviewDetail {
        ReviewDetail reviewsDetail;
        List<CommentDto> comments;
        String loginMemberName;
        String loginMemberProfileUrl;

        public ResponseReviewDetail(ReviewDetail reviewsDetail, List<CommentDto> comments, MemberEntity loginMember) {
            this.reviewsDetail = reviewsDetail;
            this.comments = comments;
            this.loginMemberName = loginMember.getName();
            this.loginMemberProfileUrl = loginMember.getProfileUrl();
        }
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReviewDetail {
        private String reviewId;
        private String reviewContent;
        private String memberName;
        private String memberMid;
        private String memberProfileUrl;
        private String placeName;
        private String placeAddress;
        private Long placeId;
        private List<String> reviewImagesUrl;
        private String reviewCreateDt;
        private int keepCount;
        private Boolean isKeep;
        private int commentCount;
        private String groupName;
        private String groupId;
        private Boolean groupIsDelete;

        @QueryProjection
        public ReviewDetail(ReviewEntity review, MemberEntity member, PlaceEntity place) {
            this.reviewId = review.getReviewId();
            this.reviewContent = review.getContent();
            this.memberName = member.getName();
            this.memberMid = member.getMid();
            this.memberProfileUrl = member.getProfileUrl();
            this.placeName = place.getPlaceName();
            this.placeAddress = place.getAddress();
            this.placeId = place.getPlaceId();
            this.reviewImagesUrl = List.of(review.getImages().split(","));
            this.reviewCreateDt = String.valueOf(review.getCreateTime());
            this.keepCount = (int) review.getReviewKeepEntities().stream().filter(ReviewKeepEntity::isKeepYn).count();
            this.isKeep = review.getReviewKeepEntities().stream().anyMatch(r -> r.getMemberMid().equals(CryptUtils.getMid()) && r.isKeepYn());
            this.commentCount = review.getReviewCommentEntities().size();
            this.groupName = review.getGroup().getName();
            this.groupId = review.getGroup().getGroupId();
            this.groupIsDelete = review.getGroup().getIsDelete();
        }
    }

}
