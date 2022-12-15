package com.teamside.project.alpha.notification.model.dto;

import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.notification.model.enumurate.NotificationType;
import lombok.*;

import java.util.List;

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
    private Boolean senderIsWithdrawal;
    private String groupId;
    private String groupName;
    private GroupMemberStatus joinStatus;
    private String reviewId;
    private String reviewTitle;
    private String dailyId;
    private String dailyTitle;
    private String imageUrl;
    private String commentId;
    private String commentContent;
    private Boolean existsImage;
    private Integer keepCnt;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyNotifications {
        private Long nextOffset;
        private List<NotificationDto> notificationList;

        public MyNotifications(List<NotificationDto> notificationList, Long nextOffset, Long pageSize) {
            this.notificationList = notificationList;
            if (notificationList.size() == pageSize) {
                if (nextOffset == null) {
                    this.nextOffset = pageSize;
                } else {
                    this.nextOffset = nextOffset + pageSize;
                }
            } else {
                this.nextOffset = null;
            }
        }
    }

    public void createMsg() {
        switch (this.getNotificationType()) {
            case KPS_GD:
                this.notiContent = "'" + this.groupName + "'그룹이 삭제되었어요.";
                break;
            case KPS_GJ:
                this.notiContent = "'" + this.senderName + "'님이 그룹에 참여했어요.";
                break;
            case KPS_GNR:
                this.notiContent = "리뷰 [" + (this.reviewTitle != null && this.reviewTitle.length() > 20 ? this.reviewTitle.substring(0, 20) + "..." : this.reviewTitle) + "] 글이 업데이트 됐어요.";
                break;
            case KPS_GND:
                this.notiContent = "일상 [" + (this.dailyTitle != null && this.dailyTitle.length() > 20 ? this.dailyTitle.substring(0, 20) + "..." : this.dailyTitle) + "] 글이 업데이트 됐어요.";
                break;
            case KPS_MRK:
                if (keepCnt > 1) {
                    this.notiContent = "'" + this.senderName + "'님 외 " + (this.keepCnt - 1) + "명이 [" + this.reviewTitle + "] 글을 킵했어요.";
                } else {
                    this.notiContent = "'" + this.senderName + "'님이 [" + this.reviewTitle + "] 글을 킵했어요.";
                }
                break;
            case KPS_MRC:
                this.notiContent = "리뷰 [" + this.reviewTitle + "]에 '" + this.senderName + "'님이 댓글을 달았어요.";
                break;
            case KPS_MDC:
                this.notiContent = "일상 [" + this.dailyTitle + "]에 '" + this.senderName + "'님이 댓글을 달았어요.";
                break;
            case KPS_MCC:
                String cmt = this.commentContent.trim();

                // 댓글 20자 확인
                cmt = cmt.length() > 20 ? cmt.substring(0, 20) + "..." : cmt;

                // 중간 개행 확인
                if (cmt.contains("\n")) {
                    cmt = cmt.substring(0, cmt.indexOf("\n")) + "...";
                }

                this.notiContent = "'" + cmt + "'댓글에 '" + this.senderName + "'님이 댓글을 달았어요.";
                break;
            case KPS_MFW:
                this.notiContent = "'" + this.senderName + "’님이 '" + this.receiverName + "’님을 팔로우했어요.";
                break;
            case KPS_FCR:
                this.notiContent = "'" + this.senderName + "’님이 새 리뷰글을 등록했어요.";
                break;
            case KPS_GI:
                this.notiContent = "'" + this.senderName + "’님이 '" + this.receiverName + "’님을 " + this.groupName + "그룹에 초대했어요.";
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
