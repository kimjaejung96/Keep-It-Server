package com.teamside.project.alpha.group.model.domain;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.place.model.entity.PlaceEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "REVIEW")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class ReviewEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID", columnDefinition = "bigint")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID",  referencedColumnName = "PLACE_ID")
    private PlaceEntity place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER",  referencedColumnName = "MID")
    private MemberEntity master;

    @Column(name = "CONTENT", columnDefinition = "varchar(1000)")
    private String content;

    @Column(name = "IMAGES", columnDefinition = "varchar(1000)")
    private String images;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewCommentEntity> reviewCommentEntities;

    public ReviewEntity(Long groupId, ReviewDto review) {
        this.group = new GroupEntity(groupId);
        this.place = new PlaceEntity(review.getPlaceId());
        this.master = new MemberEntity(CryptUtils.getMid());
        this.content = review.getContent();
        this.images = String.join(",", review.getImages());
    }

    public ReviewEntity(Long reviewId) {
        this.reviewId = reviewId;
    }

    public void updateReview(ReviewDto.UpdateReviewDto dto) {
        this.place = new PlaceEntity(dto.getPlaceId());
        this.content = dto.getContent();
        this.images = String.join(",", dto.getImages());
    }

    public void checkReviewMaster(String mid) {
        if (!this.master.getMid().equals(mid)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void createComment(CommentDto.CreateComment comment, Long reviewId) {
        this.reviewCommentEntities.add(ReviewCommentEntity.createComment(comment, reviewId));
    }
}
