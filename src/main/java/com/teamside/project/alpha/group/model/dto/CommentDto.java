package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.group.model.domain.ReviewCommentEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    public void insertChildComments(CommentDto commentDto) {
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
        this.childComments = new ArrayList<>();
    }

    // TODO: 2022/09/29 밸리데이션 확인해야함.
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateComment {
        private String comment;
        private String image;
        private Long parentCommentId;
    }
}
