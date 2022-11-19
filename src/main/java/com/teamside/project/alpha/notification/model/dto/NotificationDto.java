package com.teamside.project.alpha.notification.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.notification.model.enumurate.NotificationType;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class NotificationDto {
    private Long seq;
    private String notiDate;
    private String notiContent;
    private NotificationType notificationType;
    private String receiverMid;
    private String receiverName;
    private String senderMid;
    private String senderName;
    private String groupId;
    private String groupName;
    private String reviewId;
    private String reviewTitle;
    private String dailyId;
    private String dailyTitle;
    private String imageUrl;
    private String commentId;
    private String commentContent;

    @QueryProjection
    public NotificationDto(Long seq, String notiDate, NotificationType notificationType, String receiverMid, String receiverName, String senderMid, String senderName, String groupId, String groupName, String reviewId, String reviewTitle, String dailyId, String dailyTitle, String imageUrl, String commentId, String commentContent) {
        this.seq = seq;
        this.notiDate = notiDate;
        this.notificationType = notificationType;
        this.receiverMid = receiverMid;
        this.receiverName = receiverName;
        this.senderMid = senderMid;
        this.senderName = senderName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.reviewId = reviewId;
        this.reviewTitle = reviewTitle;
        this.dailyId = dailyId;
        this.dailyTitle = dailyTitle;
        this.imageUrl = imageUrl;
        this.commentId = commentId;
        this.commentContent = commentContent;
    }

    public void createMsg() {
        switch (this.getNotificationType()) {
            case KPS_GD:
                this.notiContent = "‘" + this.groupName + "‘그룹이 삭제되었어요.";
                break;
            case KPS_GJ:
                this.notiContent = "‘" + this.senderName + "‘님이 그룹에 참여했어요.";
                break;
            case KPS_GNR:
                this.notiContent = "리뷰 [" + this.reviewTitle + "] 글이 업데이트 됐어요.";
                break;
            case KPS_GND:
                this.notiContent = "일상 [" + this.dailyTitle + "] 글이 업데이트 됐어요.";
                break;
            case KPS_MRK:
                this.notiContent = "‘" + this.senderName + "‘님이 [" + this.reviewTitle + "] 글을 킵했어요.";
                break;
            case KPS_MRC:
                this.notiContent = "리뷰 [" + this.reviewTitle + "]에 댓글이 달렸어요.";
                break;
            case KPS_MDC:
                this.notiContent = "일상 [" + this.dailyTitle + "]에 댓글이 달렸어요.";
                break;
            case KPS_MCC:
                this.notiContent = "“" + this.commentContent + "”댓글에 답댓글이 달렸어요.";
                break;
            case KPS_MFW:
                this.notiContent = "‘" + this.senderName + "’님이 ‘" + this.receiverName + "’님을 팔로우했어요.";
                break;
            case KPS_FCR:
                this.notiContent = "‘" + this.senderName + "’님이 새 리뷰글을 등록했어요.";
                break;
            case KPS_GI:
                this.notiContent = "‘" + this.senderName + "’님이 ‘" + this.receiverName + "’님을 " + this.groupName + "그룹에 초대했어요.";
                break;
            case KPS_GE:
                this.notiContent = "그룹장에 의해 강제 탈퇴되었어요.";
                break;
            case KPS_MKT:
                this.notiContent = "";
                break;
            case KPS_UDT:
                this.notiContent = "";
                break;
        }
    }
}
