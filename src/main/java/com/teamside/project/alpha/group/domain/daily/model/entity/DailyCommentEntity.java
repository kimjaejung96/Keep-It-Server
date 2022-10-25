package com.teamside.project.alpha.group.domain.daily.model.entity;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.common.enumurate.CommentStatus;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "DAILY_COMMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@Builder
@AllArgsConstructor
public class DailyCommentEntity extends TimeEntity {
    @Id
    @Column(name = "COMMENT_ID", columnDefinition = "char(36)")
    private String commentId;

    @Column(name = "COMMENT", columnDefinition = "varchar(1500)")
    private String comment;

    @Column(name = "IMAGE_URL", columnDefinition = "varchar(255)")
    private String imageUrl;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @Column(name = "MASTER_MID", columnDefinition = "char(36)")
    private String masterMid;

    @Column(name = "TARGET_MID", columnDefinition = "char(36)")
    private String targetMemberMid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DAILY_ID", referencedColumnName = "DAILY_ID")
    private DailyEntity daily;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID", referencedColumnName = "COMMENT_ID")
    private DailyCommentEntity parentComment;

    public DailyCommentEntity(String commentId) { this.commentId = commentId; }

    public static DailyCommentEntity createComment(CommentDto.CreateComment comment, Long dailyId) {
        return DailyCommentEntity.builder()
                .commentId(UUID.randomUUID().toString())
                .comment(comment.getComment())
                .imageUrl(comment.getImage())
                .status(CommentStatus.CREATED)
                .targetMemberMid(comment.getTargetMid() != null ? comment.getTargetMid() : null)
                .masterMid(CryptUtils.getMid())
                .daily(new DailyEntity(dailyId))
                .parentComment(comment.getParentCommentId() != null ? new DailyCommentEntity(comment.getParentCommentId()) : null)
                .build();
    }

    public void checkCommentMaster(String mid) {
        if (!this.masterMid.equals(mid)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void updateComment(CommentDto.CreateComment comment) {
        this.comment = comment.getComment();
        if (comment.getImage() != null && !comment.getImage().isBlank()) {
            this.imageUrl = comment.getImage();
        }
    }

    public void deleteComment() {
        this.status = CommentStatus.DELETED;
    }
}
