package com.teamside.project.alpha.notification.model.entity;

import com.teamside.project.alpha.notification.model.enumurate.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "NOTI_LIST")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ", columnDefinition = "bigint")
    private Long seq;

    @Column(name = "NOTI_DATE", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime notiDate;

    @Column(name = "RECEIVER_MID", columnDefinition = "char(36)")
    private String receiverMid;

    @Column(name = "NOTI_TYPE", columnDefinition = "varchar(10)")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "SENDER_MID", columnDefinition = "char(36)")
    private String senderMid;

    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

    @Column(name = "REVIEW_ID", columnDefinition = "char(36)")
    private String reviewId;

    @Column(name = "DAILY_ID", columnDefinition = "char(36)")
    private String dailyId;

    @Column(name = "COMMENT_ID", columnDefinition = "char(36)")
    private String commentId;
    @Column(name = "PLACE_ID", columnDefinition = "char(36)")
    private Long placeId;
    @Column(name = "VIEW_YN", columnDefinition = "char(1)")
    private Boolean viewYn;

    @Column(name = "UPDATE_DT",nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime updateTime;
}
