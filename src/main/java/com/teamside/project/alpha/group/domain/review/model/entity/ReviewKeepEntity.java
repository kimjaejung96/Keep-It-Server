package com.teamside.project.alpha.group.domain.review.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@DynamicUpdate
@Table(name = "REVIEW_KEEP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeepEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KEEP_ID", columnDefinition = "bigint")
    private Long keepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID",  referencedColumnName = "REVIEW_ID")
    private ReviewEntity review;

    @Column(name = "MEMBER", columnDefinition = "char(36)")
    private String memberMid;

    @Column(name = "KEEP_YN", columnDefinition = "boolean")
    private boolean keepYn;

    public ReviewKeepEntity(String reviewId, String mid) {
        this.review = new ReviewEntity(reviewId);
        this.memberMid = mid;
        this.keepYn = true;
    }

    public void updateKeep() {
        this.keepYn = !this.keepYn;
    }
}
