package com.teamside.project.alpha.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
import com.teamside.project.alpha.notification.model.entity.QNotificationEntity;
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
    QNotificationEntity notification = QNotificationEntity.notificationEntity;
    @Override
    public List<NotificationEntity> getNotifications() {
        return null;
    }
}
