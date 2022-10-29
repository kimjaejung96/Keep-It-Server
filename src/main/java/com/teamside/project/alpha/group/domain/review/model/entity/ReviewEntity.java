package com.teamside.project.alpha.group.domain.review.model.entity;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.place.model.entity.PlaceEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Table(name = "REVIEW")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
public class ReviewEntity extends TimeEntity {
    @Id
    @Column(name = "REVIEW_ID", columnDefinition = "char(36)")
    private String reviewId;

    @Column(name = "SEQ", columnDefinition = "BIGINT(20) NOT NULL UNIQUE KEY auto_increment")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID",  referencedColumnName = "PLACE_ID")
    private PlaceEntity place;
    @Column(name = "MASTER", columnDefinition = "char(36)")
    private String masterMid;

    @Column(name = "CONTENT", columnDefinition = "varchar(1000)")
    private String content;

    @Column(name = "IMAGES", columnDefinition = "varchar(1000)")
    private String images;

    @Column(name = "IS_DELETE", columnDefinition = "boolean")
    private Boolean isDelete;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewCommentEntity> reviewCommentEntities;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewKeepEntity> reviewKeepEntities;

    public ReviewEntity(String groupId, ReviewDto review) {
        this.reviewId = UUID.randomUUID().toString();
        this.group = new GroupEntity(groupId);
        this.place = new PlaceEntity(review.getPlaceId());
        this.masterMid = CryptUtils.getMid();
        this.content = review.getContent();
        this.images = String.join(",", review.getImages());
        this.isDelete = false;
    }

    public ReviewEntity(String reviewId) {
        this.reviewId = reviewId;
    }

    public void updateReview(ReviewDto.UpdateReviewDto dto) {
        this.place = new PlaceEntity(dto.getPlaceId());
        this.content = dto.getContent();
        this.images = String.join(",", dto.getImages());
    }

    public void checkReviewMaster(String mid) {
        if (!this.masterMid.equals(mid)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public String createComment(CommentDto.CreateComment comment, String reviewId) {
        ReviewCommentEntity reviewCommentEntity = ReviewCommentEntity.createComment(comment, reviewId);
        this.reviewCommentEntities.add(reviewCommentEntity);
        return reviewCommentEntity.getCommentId();
    }

    public void keepReview(String reviewId, String mid) {
        // 이미 킵중이면 킵 취소
        Optional<ReviewKeepEntity> keepEntity = this.reviewKeepEntities.stream()
                .filter(keep -> (keep.getMemberMid().equals(mid) && keep.getReview().getReviewId().equals(reviewId)))
                .findFirst();

        if (keepEntity.isPresent()) {
            this.getReviewKeepEntities().remove(keepEntity.get());
        } else {
            this.getReviewKeepEntities().add(new ReviewKeepEntity(reviewId, mid));
        }
    }

    public void deleteReview() {
        this.isDelete = true;
    }
}
