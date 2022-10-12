package com.teamside.project.alpha.group.domain.review.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER",  referencedColumnName = "MID")
    private MemberEntity member;

    public ReviewKeepEntity(Long reviewId, String mid) {
        this.review = new ReviewEntity(reviewId);
        this.member = new MemberEntity(mid);
    }
}
