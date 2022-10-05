package com.teamside.project.alpha.group.model.domain;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.enumurate.CommentStatus;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "DAILY_COMMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@Builder
@AllArgsConstructor
public class DailyCommentEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID", columnDefinition = "bigint")
    private Long commentId;

    @Column(name = "COMMENT", columnDefinition = "varchar(1500)")
    private String comment;

    @Column(name = "IMAGE_URL", columnDefinition = "varchar(255)")
    private String imageUrl;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER_MID", referencedColumnName = "MID")
    private MemberEntity master;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_MID", referencedColumnName = "MID")
    private MemberEntity targetMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DAILY_ID", referencedColumnName = "DAILY_ID")
    private DailyEntity daily;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID", referencedColumnName = "COMMENT_ID")
    private DailyCommentEntity parentComment;

    public DailyCommentEntity(Long commentId) { this.commentId = commentId; }

    public static DailyCommentEntity createComment(CommentDto.CreateComment comment, Long dailyId) {
        return DailyCommentEntity.builder()
                .comment(comment.getComment())
                .imageUrl(comment.getImage())
                .status(CommentStatus.CREATED)
                .targetMember(comment.getTargetMid() != null ? new MemberEntity(comment.getTargetMid()) : null)
                .master(new MemberEntity(CryptUtils.getMid()))
                .daily(new DailyEntity(dailyId))
                .parentComment(comment.getParentCommentId() != null ? new DailyCommentEntity(comment.getParentCommentId()) : null)
                .build();
    }

    public void checkCommentMaster(String mid) {
        if (!this.master.getMid().equals(mid)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void updateComment(CommentDto.CreateComment comment) {
        this.comment = comment.getComment();
        this.imageUrl = comment.getImage();
        this.targetMember = (comment.getTargetMid() != null ? new MemberEntity(comment.getTargetMid()) : null);
    }

    public void deleteComment() {
        this.status = CommentStatus.DELETED;
    }
}
