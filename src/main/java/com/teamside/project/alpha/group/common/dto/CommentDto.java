package com.teamside.project.alpha.group.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.group.common.enumurate.CommentStatus;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewCommentEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentDto {
    private String commentId;
    private String memberName;
    private String memberId;
    private String memberProfileUrl;
    private String comment;
    private String createDt;
    private String parentCommentId;
    private List<CommentDto> childComments;
    private String imageUrl;
    private String targetName;
    private String targetMid;
    private CommentStatus status;
    public void insertChildComments(CommentDto commentDto) {
        this.childComments.add(commentDto);
    }

    public void filterBlockComment(List<String> blocks) {
        if (blocks.contains(this.getMemberId())) {
            this.comment = "차단한 사용자의 댓글입니다.";
        }
    }

    @QueryProjection
    public CommentDto(ReviewCommentEntity reviewComment, MemberEntity member, MemberEntity targetMember) {
        this.childComments = new ArrayList<>();
        this.commentId = reviewComment.getCommentId();
        this.memberName = member.getName();
        this.memberId = member.getMid();
        this.memberProfileUrl = member.getProfileUrl();
        this.createDt = String.valueOf(reviewComment.getCreateTime());
        this.parentCommentId = reviewComment.getParentComment() != null?reviewComment.getParentComment().getCommentId():null;
        this.imageUrl = reviewComment.getImageUrl();
        this.targetName = targetMember != null ? targetMember.getName() : null;
        this.targetMid = targetMember != null ? targetMember.getMid() : null;
        this.status = reviewComment.getStatus();
        if (reviewComment.getStatus().equals(CommentStatus.BLOCKED)) {
            this.comment = "신고에 의해 숨김 처리된 댓글입니다.";
        } else if (reviewComment.getStatus().equals(CommentStatus.DELETED)) {
            this.comment = "삭제된 댓글입니다.😢";
        } else {
            this.comment = reviewComment.getComment();
        }
    }

    @QueryProjection
    public CommentDto(DailyCommentEntity dailyComment, MemberEntity member, MemberEntity targetMember) {
        this.childComments = new ArrayList<>();
        this.commentId = dailyComment.getCommentId();
        this.memberName = member.getName();
        this.memberId = member.getMid();
        this.memberProfileUrl = member.getProfileUrl();
        this.createDt = String.valueOf(dailyComment.getCreateTime());
        this.parentCommentId = dailyComment.getParentComment() != null ? dailyComment.getParentComment().getCommentId() : null;
        this.imageUrl = dailyComment.getImageUrl();
        this.targetName = targetMember != null ? targetMember.getName() : null;
        this.targetMid = targetMember != null ? targetMember.getMid() : null;
        this.status = dailyComment.getStatus();
        if (dailyComment.getStatus().equals(CommentStatus.BLOCKED)) {
            this.comment = "신고에 의해 숨김 처리된 댓글입니다.";
        } else if (dailyComment.getStatus().equals(CommentStatus.DELETED)) {
            this.comment = "삭제된 댓글입니다.😢";
        } else {
            this.comment = dailyComment.getComment();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateComment {
        @Size(min = 1, max = 1500, message = "댓글 내용은 1자~1500자 사이입니다.")
        private String comment;
        private String image;
        private String parentCommentId;
        private String targetMid;
        private String targetCommentId;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentDetail {
        private Integer nextOffset;
        private List<CommentDto> comments;

        public CommentDetail(Integer nextOffset,int limit, List<CommentDto> comments) {
            if (nextOffset == null || nextOffset == 0) {
                this.nextOffset = limit+1;
            } else if (comments.size() == limit) {
                this.nextOffset = nextOffset + limit + 1;
            } else this.nextOffset = null;
            this.comments = comments;
        }
    }

}
