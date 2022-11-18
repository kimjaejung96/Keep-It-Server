package com.teamside.project.alpha.notification.model.dto;

import com.teamside.project.alpha.notification.model.enumurate.NotificationType;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class NotificationDto {
    private String notiDate;
    private String notiContent;
    private String receiverMid;
    private String receiverName;
    private NotificationType notificationType;
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
}
