package com.teamside.project.alpha.group.domain.review.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.common.enumurate.CommentStatus;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "REVIEW_COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
@Builder
@AllArgsConstructor
public class ReviewCommentEntity extends TimeEntity {
    @Id
    @Column(name = "COMMENT_ID", columnDefinition = "char(36)")
    private String commentId;

    @Column(name = "SEQ", columnDefinition = "BIGINT(20) NOT NULL UNIQUE KEY auto_increment")
    private Long seq;

    @Column(name = "COMMENT", columnDefinition = "varchar(1500)")
    private String comment;

    @Column(name = "IMAGE_URL", columnDefinition = "varchar(255)")
    private String imageUrl;

    @Column(name = "MASTER_MID", columnDefinition = "char(36)")
    private String masterMid;

    @Column(name = "TARGET_MID", columnDefinition = "char(36)")
    private String targetMemberMid;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", referencedColumnName = "REVIEW_ID")
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID", referencedColumnName = "COMMENT_ID")
    private ReviewCommentEntity parentComment;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    public static ReviewCommentEntity createComment (CommentDto.CreateComment comment, String reviewId) {
        return ReviewCommentEntity.builder()
                .commentId(UUID.randomUUID().toString())
                .comment(comment.getComment())
                .imageUrl(comment.getImage())
                .masterMid(CryptUtils.getMid())
                .review(new ReviewEntity(reviewId))
                .targetMemberMid(comment.getTargetMid() != null ? comment.getTargetMid() : null)
                .parentComment(comment.getParentCommentId() != null ? new ReviewCommentEntity(comment.getParentCommentId()) : null)
                .status(CommentStatus.CREATED)
                .build();
    }

    public ReviewCommentEntity(String commentId) {
        this.commentId = commentId;
    }

    public void updateComment(CommentDto.CreateComment comment) {
        this.comment = comment.getComment();
        this.imageUrl = comment.getImage();
    }

    public void deleteComment() {
        this.status = CommentStatus.DELETED;
    }
}
