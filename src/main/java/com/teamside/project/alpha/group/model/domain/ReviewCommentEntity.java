package com.teamside.project.alpha.group.model.domain;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "REVIEW_COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Builder
@AllArgsConstructor
public class ReviewCommentEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID", columnDefinition = "bigint")
    private Long commentId;

    @Column(name = "COMMENT", columnDefinition = "varchar(1500)")
    private String comment;

    @Column(name = "IMAGE_URL", columnDefinition = "varchar(255)")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER_MID", referencedColumnName = "MID")
    private MemberEntity master;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", referencedColumnName = "REVIEW_ID")
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID", referencedColumnName = "COMMENT_ID")
    private ReviewCommentEntity parentComment;

    public static ReviewCommentEntity createComment (CommentDto.CreateComment comment, Long reviewId) {
        return ReviewCommentEntity.builder()
                .comment(comment.getComment())
                .imageUrl(comment.getImage())
                .master(new MemberEntity(CryptUtils.getMid()))
                .review(new ReviewEntity(reviewId))
                .build();
    }

    public ReviewCommentEntity(Long commentId) {
        this.commentId = commentId;
    }

    public static ReviewCommentEntity createCoComment (CommentDto.CreateComment comment, Long reviewId) {
        return ReviewCommentEntity.builder()
                .comment(comment.getComment())
                .imageUrl(comment.getImage())
                .master(new MemberEntity(CryptUtils.getMid()))
                .review(new ReviewEntity(reviewId))
                .parentComment(new ReviewCommentEntity(comment.getParentCommentId()))
                .build();
    }
}
