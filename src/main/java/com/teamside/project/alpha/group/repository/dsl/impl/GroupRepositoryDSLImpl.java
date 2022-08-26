package com.teamside.project.alpha.group.repository.dsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.group.repository.dsl.GroupRepositoryDSL;

public class GroupRepositoryDSLImpl implements GroupRepositoryDSL {
    private final JPAQueryFactory jpaQueryFactory;

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
}
