package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.group.model.domain.ReviewCommentEntity;
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
    public void insertChildComments(CommentDto commentDto) {
        this.childComments = new ArrayList<>();
        this.childComments.add(commentDto);
    }

    @QueryProjection
    public CommentDto(ReviewCommentEntity reviewComment, MemberEntity member) {
        this.commentId = reviewComment.getCommentId();
        this.memberName = member.getName();
        this.memberId = member.getMid();
        this.memberProfileUrl = member.getProfileUrl();
        this.comment = reviewComment.getComment();
        this.createDt = String.valueOf(reviewComment.getCreateTime());
        this.parentCommentId = reviewComment.getParentComment() != null?reviewComment.getParentComment().getCommentId():null;
        this.imageUrl = reviewComment.getImageUrl();
        this.targetName = reviewComment.getTargetMember() != null ? reviewComment.getTargetMember().getName() : null;
        this.targetMid = reviewComment.getTargetMember() != null ? reviewComment.getTargetMember().getMid() : null;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateComment {
        @Size(min = 15, max = 2000, message = "댓글 내용은 15자~2000자 사이입니다.")
        private String comment;
        private String image;
        private Long parentCommentId;
        private String targetMid;
    }
}
