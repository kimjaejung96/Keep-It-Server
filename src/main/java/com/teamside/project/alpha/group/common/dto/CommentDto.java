package com.teamside.project.alpha.group.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.group.common.enumurate.CommentStatus;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewCommentEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDto {
    private Long commentId;
    private String memberName;
    private String memberId;
    private String memberProfileUrl;
    private String comment;
    private String createDt;
    private Long parentCommentId;
    private List<CommentDto> childComments;
    private String imageUrl;
    private String targetName;
    private String targetMid;
    private CommentStatus status;
    public void insertChildComments(CommentDto commentDto) {
        this.childComments.add(commentDto);
    }

    @QueryProjection
    public CommentDto(ReviewCommentEntity reviewComment, MemberEntity member, MemberEntity targetMember) {
        this.childComments = new ArrayList<>();
        this.commentId = reviewComment.getCommentId();
        this.memberName = member.getName();
        this.memberId = member.getMid();
        this.memberProfileUrl = member.getProfileUrl();
        this.comment = reviewComment.getStatus().equals(CommentStatus.BLOCKED) ? "신고에 의해 숨김 처리된 댓글입니다." : reviewComment.getComment();
        this.createDt = String.valueOf(reviewComment.getCreateTime());
        this.parentCommentId = reviewComment.getParentComment() != null?reviewComment.getParentComment().getCommentId():null;
        this.imageUrl = reviewComment.getImageUrl();
        this.targetName = targetMember.getName();
        this.targetMid = targetMember.getMid();
        this.status = reviewComment.getStatus();
    }

    @QueryProjection
    public CommentDto(DailyCommentEntity dailyComment, MemberEntity member) {
        this.childComments = new ArrayList<>();
        this.commentId = dailyComment.getCommentId();
        this.memberName = member.getName();
        this.memberId = member.getMid();
        this.memberProfileUrl = member.getProfileUrl();
        this.comment = dailyComment.getStatus().equals(CommentStatus.BLOCKED) ? "신고에 의해 숨김 처리된 댓글입니다." : dailyComment.getComment();
        this.createDt = String.valueOf(dailyComment.getCreateTime());
        this.parentCommentId = dailyComment.getParentComment() != null ? dailyComment.getParentComment().getCommentId() : null;
        this.imageUrl = dailyComment.getImageUrl();
        this.targetName = dailyComment.getTargetMember() != null ? dailyComment.getTargetMember().getName() : null;
        this.targetMid = dailyComment.getTargetMember() != null ? dailyComment.getTargetMember().getMid() : null;
        this.status = dailyComment.getStatus();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateComment {
        @Size(min = 1, max = 1500, message = "댓글 내용은 1자~1500자 사이입니다.")
        private String comment;
        private String image;
        private Long parentCommentId;
        private String targetMid;
    }
}
