package com.teamside.project.alpha.member.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyKeepEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewKeepEntity;
import com.teamside.project.alpha.group.model.entity.QGroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.QMemberFollowEntity;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyPageHome;
import com.teamside.project.alpha.member.domain.mypage.model.dto.QMyPageHome;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.dto.QMemberDto_InviteMemberList;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;

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
    public MyPageHome getMyPageHome() {
        return jpaQueryFactory
                .select(new QMyPageHome(member.mid,
                        member.name,
                        member.profileUrl,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(review.count().coalesce(0L))
                                        .from(review)
                                        .where(review.isDelete.eq(false), review.masterMid.eq(member.mid))
                        , "reviewCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(daily.count().coalesce(0L))
                                        .from(daily)
                                        .where(daily.isDelete.eq(false), daily.masterMid.eq(member.mid))
                                , "dailyCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(reviewKeep.count().coalesce(0L))
                                        .from(reviewKeep)
                                        .where(reviewKeep.keepYn.eq(true), reviewKeep.memberMid.eq(member.mid))
                                , "reviewKeepCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(dailyKeep.count().coalesce(0L))
                                        .from(dailyKeep)
                                        .where(dailyKeep.keepYn.eq(true), dailyKeep.memberMid.eq(member.mid))
                                , "dailyKeepCount"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(follow.count().coalesce(0L))
                                        .from(follow)
                                        .where(follow.mid.eq(member.mid), follow.followYn.eq(true))
                                , "followCount")
                        ))
                .from(member)
                .where(member.mid.eq(CryptUtils.getMid()))
                .fetchOne();
    }
}
