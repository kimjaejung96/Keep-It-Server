package com.teamside.project.alpha.notification.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyCommentEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.model.dto.QNotificationDto;
import com.teamside.project.alpha.notification.model.entity.QNotificationEntity;
import com.teamside.project.alpha.notification.model.enumurate.NotificationType;
import com.teamside.project.alpha.place.model.entity.QPlaceEntity;

import java.util.List;

public class NotificationRepositoryDSLImpl implements NotificationRepositoryDSL{
    private final JPAQueryFactory jpaQueryFactory;

    public NotificationRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    QMemberEntity member = QMemberEntity.memberEntity;
    QGroupEntity group = QGroupEntity.groupEntity;
    QReviewEntity review = QReviewEntity.reviewEntity;
    QDailyEntity daily = QDailyEntity.dailyEntity;
    QPlaceEntity place = QPlaceEntity.placeEntity;
    QReviewCommentEntity reviewComment = QReviewCommentEntity.reviewCommentEntity;
    QDailyCommentEntity dailyComment = QDailyCommentEntity.dailyCommentEntity;
    QNotificationEntity notification = QNotificationEntity.notificationEntity;
    @Override
    public List<NotificationDto> getNotifications(Long pageSize, Long lastSeq) {
        // KPS_GD, KPS_GJ >> 그룹 대표이미지
        // KPS_GNR, KPS_GND, KPS_MRK, KPS_MRC, KPS_MDC, KPS_MCC, KPS_MFW, KPS_FCR, KPS_GI >> sender profile
        // KPS_GE, KPS_MKT, KPS_UDT >> icon
        QMemberEntity senderMember = new QMemberEntity("senderMember");

        return jpaQueryFactory
                .select(new QNotificationDto(
                        notification.seq,
                        notification.notiDate.stringValue(),
                        notification.notificationType,
                        member.mid,
                        member.name,
                        senderMember.mid,
                        senderMember.name,
                        group.groupId,
                        group.name,
                        review.reviewId,
                        place.placeName,
                        daily.dailyId,
                        daily.title,
                        new CaseBuilder()
                                .when(notification.notificationType.in(NotificationType.KPS_GD, NotificationType.KPS_GJ))
                                .then(group.profileUrl)
                                .when(notification.notificationType.in(NotificationType.KPS_GE, NotificationType.KPS_MKT, NotificationType.KPS_UDT))
                                .then(Expressions.nullExpression())
                                .otherwise(senderMember.profileUrl)
                                .as("imageUrl"),
                        new CaseBuilder()
                                .when(notification.reviewId.isNotNull())
                                .then(reviewComment.commentId)
                                .when(notification.dailyId.isNotNull())
                                .then(dailyComment.commentId)
                                .otherwise(Expressions.nullExpression())
                                .as("commentId"),
                        new CaseBuilder()
                                .when(notification.reviewId.isNotNull())
                                .then(reviewComment.comment)
                                .when(notification.dailyId.isNotNull())
                                .then(dailyComment.comment)
                                .otherwise(Expressions.nullExpression())
                                .as("commentContent")
                ))
                .from(notification)
                .leftJoin(member).on(notification.receiverMid.eq(member.mid))
                .leftJoin(senderMember).on(notification.senderMid.eq(senderMember.mid))
                .leftJoin(group).on(notification.groupId.eq(group.groupId))
                .leftJoin(review).on(notification.reviewId.eq(review.reviewId))
                .leftJoin(place).on(review.place.placeId.eq(place.placeId))
                .leftJoin(daily).on(notification.dailyId.eq(daily.dailyId))
                .leftJoin(reviewComment).on(notification.commentId.eq(reviewComment.commentId))
                .leftJoin(dailyComment).on(notification.commentId.eq(dailyComment.commentId))
                .where(notification.receiverMid.eq(CryptUtils.getMid()), ltSeq(lastSeq))
                .orderBy(notification.seq.desc())
                .limit(pageSize)
                .fetch();
    }

    public BooleanExpression ltSeq(Long lastSeq) {
        return lastSeq != null ? notification.seq.lt(lastSeq) : null;
    }
}
