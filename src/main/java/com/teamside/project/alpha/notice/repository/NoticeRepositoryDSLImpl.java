package com.teamside.project.alpha.notice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.notice.model.dto.NoticeDto;
import com.teamside.project.alpha.notice.model.dto.QNoticeDto_Notice;
import com.teamside.project.alpha.notice.model.entity.QNoticeEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class NoticeRepositoryDSLImpl implements NoticeRepositoryDSL {
    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    public NoticeRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    QNoticeEntity notice = QNoticeEntity.noticeEntity;

    @Override
    public List<NoticeDto.Notice> getNotices(Long pageSize, Long lastNoticeId) {
        return jpaQueryFactory
                .select(new QNoticeDto_Notice(
                        notice.noticeId,
                        notice.title,
                        notice.createTime.stringValue()))
                .from(notice)
                .where(notice.type.ne("MARKETING"),
                        ltNoticeId(lastNoticeId))
                .limit(pageSize)
                .orderBy(notice.noticeId.desc())
                .fetch();
    }
    public BooleanExpression ltNoticeId(Long lastNoticeId) {
        return lastNoticeId != null ? notice.noticeId.lt(lastNoticeId) : null;
    }
}
