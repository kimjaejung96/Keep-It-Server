package com.teamside.project.alpha.group.repository.dsl.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.group.domain.QGroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.dto.QGroupDto_MyGroupDto;
import com.teamside.project.alpha.group.model.dto.QGroupDto_SearchGroupDto;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.group.repository.dsl.GroupRepositoryDSL;

import java.util.List;

import static com.querydsl.core.types.ExpressionUtils.count;

public class GroupRepositoryDSLImpl implements GroupRepositoryDSL {
    private final JPAQueryFactory jpaQueryFactory;

    QGroupEntity group = QGroupEntity.groupEntity;
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;

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
    public List<GroupDto.SearchGroupDto> selectGroups(Long groupId, Long pageSize) {
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
                        gtGroupId(groupId)
                )
                .limit(pageSize)
                .groupBy(group.groupId)
                .fetch();
    }

    public BooleanExpression gtGroupId(Long groupId) {
        return groupId != null ? group.groupId.gt(groupId) : null;
    }

    @Override
    public List<GroupDto.MyGroupDto> selectMyGroups(String mId) {
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
                .where(groupMemberMapping.member.mid.eq(mId))
                .fetch();
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
}
