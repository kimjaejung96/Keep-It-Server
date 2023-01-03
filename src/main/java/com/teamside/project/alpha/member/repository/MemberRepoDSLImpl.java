package com.teamside.project.alpha.member.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyKeepEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewKeepEntity;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.group.model.entity.QGroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.QMemberFollowEntity;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyPageHome;
import com.teamside.project.alpha.member.domain.mypage.model.dto.QMyPageHome;
import com.teamside.project.alpha.member.domain.noti_check.model.entity.dto.NotificationCheckDTO;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.dto.QMemberDto_InviteMemberList;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MemberRepoDSLImpl implements MemberRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    QMemberEntity member = QMemberEntity.memberEntity;
    QReviewKeepEntity reviewKeep = QReviewKeepEntity.reviewKeepEntity;
    QDailyKeepEntity dailyKeep = QDailyKeepEntity.dailyKeepEntity;
    QReviewEntity review = QReviewEntity.reviewEntity;
    QDailyEntity daily = QDailyEntity.dailyEntity;
    QMemberFollowEntity follow = QMemberFollowEntity.memberFollowEntity;
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;
    QGroupEntity group = QGroupEntity.groupEntity;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<List<MemberDto.InviteMemberList>> searchMembers(String name, String groupId) {
        List<MemberDto.InviteMemberList> result;
        if (groupId == null) {
            result = jpaQueryFactory
                    .select(new QMemberDto_InviteMemberList(member.name, member.mid))
                    .from(member)
                    .where(member.name.contains(name).and(member.mid.ne(CryptUtils.getMid())))
                    .fetch();
        } else {
            result = jpaQueryFactory
                    .select(new QMemberDto_InviteMemberList(member.name, member.mid))
                    .from(member)
                    .leftJoin(groupMemberMapping).on(groupMemberMapping.mid.eq(member.mid))
                    .where((groupMemberMapping.groupId.ne(groupId).or(groupMemberMapping.groupId.isNull()))
                            .and(member.name.contains(name))
                            .and(member.mid.ne(CryptUtils.getMid())))
                    .groupBy(member.name, member.mid)
                    .fetch();
        }
        return Optional.ofNullable(result);
    }

    @Override
    public MyPageHome getMyPageHome(List<String> blocks) {
        return jpaQueryFactory
                .select(new QMyPageHome(member.mid,
                        member.name,
                        member.profileUrl,
                        member.phone,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(review.count().coalesce(0L))
                                        .from(review)
                                        .innerJoin(review.group, group)
                                        .innerJoin(group.groupMemberMappingEntity, groupMemberMapping)
                                        .where(review.isDelete.eq(false), review.masterMid.eq(member.mid),
                                                groupMemberMapping.mid.eq(member.mid), groupMemberMapping.status.eq(GroupMemberStatus.JOIN))
                        , "reviewCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(daily.count().coalesce(0L))
                                        .from(daily)
                                        .innerJoin(daily.group, group)
                                        .innerJoin(group.groupMemberMappingEntity, groupMemberMapping)
                                        .where(daily.isDelete.eq(false), daily.masterMid.eq(member.mid),
                                                groupMemberMapping.mid.eq(member.mid), groupMemberMapping.status.eq(GroupMemberStatus.JOIN))
                                , "dailyCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(reviewKeep.count().coalesce(0L))
                                        .from(reviewKeep)
                                        .innerJoin(reviewKeep.review, review)
                                        .where(reviewKeep.keepYn.eq(true),
                                                reviewKeep.memberMid.eq(member.mid),
                                                review.masterMid.notIn(blocks))
                                , "reviewKeepCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(dailyKeep.count().coalesce(0L))
                                        .from(dailyKeep)
                                        .innerJoin(dailyKeep.daily, daily)
                                        .where(dailyKeep.keepYn.eq(true),
                                                dailyKeep.memberMid.eq(member.mid),
                                                daily.masterMid.notIn(blocks))
                                , "dailyKeepCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(follow.count().coalesce(0L))
                                        .from(follow)
                                        .innerJoin(follow.group, group)
                                        .where(follow.mid.eq(member.mid),
                                                follow.followYn.eq(true),
                                                group.isDelete.eq(false))
                                , "followCount")
                        ))
                .from(member)
                .where(member.mid.eq(CryptUtils.getMid()))
                .fetchOne();
    }

    @Override
    public void unfollow(String mid, String targetMid) {
        jpaQueryFactory
                .update(follow)
                .set(follow.followYn, false)
                .set(follow.updateTime, LocalDateTime.now())
                .where(follow.mid.eq(mid),
                        follow.targetMid.eq(targetMid),
                        follow.followYn.eq(true))
                .execute();
    }

    @Override
    public NotificationCheckDTO notiCheck(String mid, LocalDateTime checkActTime, LocalDateTime checkNewsTime) {
        String queryString = "SELECT SUM(IF(A.ACT_CHECK = 1, 1, 0)) > 0 AS ACT_CHECK\n" +
                ", SUM(IF(A.NEWS_CHECK = 1, 1, 0)) > 0 AS NEWS_CHECK\n" +
                "FROM (\n" +
                "SELECT COUNT(NL.SEQ) > 0 AS 'ACT_CHECK'\n" +
                ", 0 AS 'NEWS_CHECK'\n" +
                "FROM noti_list NL\n" +
                "WHERE NL.RECEIVER_MID = ?\n" +
                "AND NL.NOTI_DATE BETWEEN ? AND NOW()\n" +
                "UNION ALL\n" +
                "SELECT COUNT(RKNL.SEQ) > 0 AS 'ACT_CHECK'\n" +
                ", 0 AS 'NEWS_CHECK'\n" +
                "FROM review_keep_noti_list RKNL\n" +
                "WHERE RKNL.RECEIVER_MID = ?\n" +
                "AND RKNL.NOTI_DATE BETWEEN ? AND NOW()\n" +
                "UNION ALL\n" +
                "SELECT 0 AS 'ACT_CHECK'\n" +
                ", COUNT(MNL.SEQ) > 0 AS 'NEWS_CHECK'\n" +
                "FROM marketing_noti_list MNL\n" +
                "WHERE MNL.NOTI_DATE BETWEEN ? AND NOW()\n" +
                "AND MNL.STATUS = 3\n" +
                "UNION ALL\n" +
                "SELECT 0 AS 'ACT_CHECK'\n" +
                ", COUNT(UNL.SEQ) > 0 AS 'NEWS_CHECK'\n" +
                "FROM update_noti_list UNL\n" +
                "WHERE UNL.NOTI_DATE BETWEEN ? AND NOW()\n" +
                "AND UNL.STATUS = 3\n" +
                ") A";

        Query query =  entityManager.createNativeQuery(queryString);

        List<Object[]> resultSets = query
                .setParameter(1, mid)
                .setParameter(2, checkActTime)
                .setParameter(3, mid)
                .setParameter(4, checkActTime)
                .setParameter(5, checkNewsTime)
                .setParameter(6, checkNewsTime)
                .getResultList();

        Boolean act = String.valueOf(resultSets.get(0)[0]).equals("1");
        Boolean news = String.valueOf(resultSets.get(0)[1]).equals("1");
        
        return new NotificationCheckDTO(act, news);
    }
}
