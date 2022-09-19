package com.teamside.project.alpha.group.repository.dsl.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.domain.QGroupMemberMappingEntity;
import com.teamside.project.alpha.group.domain.QReviewEntity;
import com.teamside.project.alpha.group.model.dto.*;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.group.repository.dsl.GroupRepositoryDSL;
import com.teamside.project.alpha.member.domain.QMemberFollowEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GroupRepositoryDSLImpl implements GroupRepositoryDSL {
    private final JPAQueryFactory jpaQueryFactory;

    QGroupEntity group = QGroupEntity.groupEntity;
    QReviewEntity review = QReviewEntity.reviewEntity;
    QMemberEntity member = QMemberEntity.memberEntity;
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;
    QMemberFollowEntity memberFollow = QMemberFollowEntity.memberFollowEntity;

    public GroupRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

//    @Override
//    public void groupNameCheckOnce(String groupId, String checkName) throws CustomException {
//        QGroupEntity groupEntity = QGroupEntity.groupEntity;
//
//        boolean exists = jpaQueryFactory.select(groupEntity.groupId).from(groupEntity)
//                .where(groupEntity.groupId.ne(groupId))
//                .where(groupEntity.name.eq(checkName))
//                .fetchFirst() != null;
//        if (exists) {
//            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
//        }
//    }

//    @Override
//    public void groupNameCheck(String groupName) throws CustomException {
//        QGroupEntity groupEntity1 = new QGroupEntity("group1");
//        QGroupEntity groupEntity2 = new QGroupEntity("group2");
//
//        boolean exists = jpaQueryFactory
//                .select(groupEntity1.name)
//                .from(groupEntity1)
//                .innerJoin(groupEntity2)
//                .on(groupEntity2.groupId.eq(groupEntity1.groupId))
//                .where(groupEntity1.name.eq(groupName))
//                .fetchFirst() != null;
//        if (exists) {
//            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
//        }
//
//    }

    @Override
    public List<GroupDto.SearchGroupDto> selectGroups(Long lastGroupId, Long pageSize, String search) {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .where(
                        gtGroupId(lastGroupId),
                        containSearch(search)
                )
                .limit(pageSize)
                .groupBy(group.groupId, group.name, group.category, group.category, group.profileUrl, group.usePrivate)
                .orderBy(group.groupId.asc())
                .fetch();
    }

    public BooleanExpression gtGroupId(Long lastGroupId) {
        return lastGroupId != null ? group.groupId.gt(lastGroupId) : null;
    }

    public BooleanExpression containSearch(String search) {
        return (search != null && !Strings.isBlank(search)) ? group.name.contains(search) : null;
    }

    @Override
    public List<GroupDto.MyGroupDto> selectMyGroups(String mId, MyGroupType type) {
        return jpaQueryFactory
                .select(new QGroupDto_MyGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(groupMemberMapping.count())
                                        .from(groupMemberMapping)
                                        .where(groupMemberMapping.groupId.eq(group.groupId)),
                        "participantCount"),
                        groupMemberMapping.favorite,
                        new CaseBuilder()
                                .when(group.master.mid.eq(groupMemberMapping.mid))
                                .then(true)
                                .otherwise(false)
                                .as("isMaster"),
                        groupMemberMapping.ord
                    ))
                .from(groupMemberMapping)
                .innerJoin(group).on(groupMemberMapping.groupId.eq(group.groupId))
                .where(
                        groupMemberMapping.member.mid.eq(mId),
                        isFavorite(type))
                .fetch();
    }

    public BooleanExpression isFavorite(MyGroupType type) {
        return type == MyGroupType.FAVORITE ? groupMemberMapping.favorite.eq(true) : null;
    }
    @Override
    public List<GroupDto.SearchGroupDto> random() {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .orderBy(Expressions.numberTemplate(Long.class,"function('rand')").asc())
                .limit(10)
                .groupBy(group.groupId)
                .fetch();
    }

    @Override
    public Optional<GroupMemberMappingEntity> selectGroupMemberMappingEntity(String mid, Long groupId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(groupMemberMapping)
                .from(group)
                .innerJoin(groupMemberMapping)
                    .on(group.groupId.eq(groupMemberMapping.groupId))
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.groupId.eq(groupId)))
                .fetchOne());
    }

    @Override
    public Optional<Integer> selectLatestFavoriteOrd(String mid) {
        return Optional.ofNullable(jpaQueryFactory
                .select(groupMemberMapping.ord)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.favorite.eq(true)))
                .orderBy(groupMemberMapping.ord.desc())
                .fetchFirst());
    }

    @Override
    public List<GroupMemberMappingEntity> selectFavoriteMappingGroups(String mid) {
        return jpaQueryFactory
                .select(groupMemberMapping)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.favorite.eq(true)))
                .fetch();
    }

    @Override
    public GroupDto.GroupInfoDto selectGroup(Long groupId) {
        GroupDto.GroupInfoDto groupInfoDto = jpaQueryFactory.select(
                        new QGroupDto_GroupInfoDto(
                                group,
                                group.master.mid,
                                groupMemberMapping.favorite,
                                JPAExpressions
                                        .select(groupMemberMapping.count())
                                        .from(groupMemberMapping)
                                        .where(groupMemberMapping.groupId.eq(groupId)),
                                JPAExpressions
                                        .select(review.count())
                                        .from(review)
                                        .where(review.group.groupId.eq(groupId))
                        )
                )
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .where(group.groupId.eq(groupId)).distinct()
                .fetchOne();


        if (groupInfoDto == null) throw new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND);

        Objects.requireNonNull(groupInfoDto).addGroupInfoMembers(
                jpaQueryFactory
                        .select(new QGroupDto_GroupInfoDto_MembersDto(
                                member.name,
                                member.mid,
                                member.profileUrl,
                                new CaseBuilder().
                                                when(new CaseBuilder()
                                                .when(member.mid.eq(memberFollow.targetMid))
                                                .then("")
                                                .otherwise("eqauls").isEmpty())
                                        .then("")
                                        .otherwise("not exist").isEmpty()
                        ))
                        .from(groupMemberMapping)
                        .innerJoin(member).on(groupMemberMapping.mid.eq(member.mid))
                        .leftJoin(memberFollow).on(memberFollow.mid.eq(CryptUtils.getMid()).and(memberFollow.targetMid.eq(member.mid)))
                        .where(groupMemberMapping.groupId.eq(groupId).and(member.mid.ne(CryptUtils.getMid())))
                        .fetch()
        );

        return groupInfoDto;
    }
}
