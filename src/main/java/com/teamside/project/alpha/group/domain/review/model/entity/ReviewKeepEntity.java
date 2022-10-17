package com.teamside.project.alpha.group.domain.review.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "REVIEW_KEEP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeepEntity extends CreateDtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KEEP_ID", columnDefinition = "bigint")
    private Long keepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID",  referencedColumnName = "REVIEW_ID")
    private ReviewEntity review;

    @Column(name = "MEMBER", columnDefinition = "char(36)")
    private String memberMid;

    public ReviewKeepEntity(Long reviewId, String mid) {
        this.review = new ReviewEntity(reviewId);
        this.memberMid = mid;
    }
}
