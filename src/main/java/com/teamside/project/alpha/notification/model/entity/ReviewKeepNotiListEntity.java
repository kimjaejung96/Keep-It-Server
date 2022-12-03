package com.teamside.project.alpha.notification.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "REVIEW_KEEP_NOTI_LIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = {AuditingEntityListener.class})
public class ReviewKeepNotiListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ", columnDefinition = "bigint")
    private Long seq;

    @Column(name = "RECEIVER_MID", columnDefinition = "char(36)")
    private String receiverMid;

    @Column(name = "LAST_KEEP_MID", columnDefinition = "char(36)")
    private String lastKeepMid;

    @Column(name = "KEEP_CNT", columnDefinition = "int")
    private Integer keepCnt;

    @Column(name = "PLACE_ID", columnDefinition = "bigint")
    private Long placeId;

    @Column(name = "REVIEW_ID", columnDefinition = "char(36)")
    private String reviewId;

    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

    @Column(name = "VIEW_YN", columnDefinition = "boolean")
    private Boolean viewYn;

    @Column(name = "NOTI_DATE", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime notiDate;

    @Column(name = "UPDATE_DT",nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime updateTime;
}
