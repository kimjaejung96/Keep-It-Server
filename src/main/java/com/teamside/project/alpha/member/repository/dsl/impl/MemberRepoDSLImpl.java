package com.teamside.project.alpha.member.repository.dsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.QGroupMemberMappingEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.member.repository.dsl.MemberRepoDSL;

import java.util.List;
import java.util.Optional;

public class MemberRepoDSLImpl implements MemberRepoDSL {
    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepoDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    QMemberEntity member = QMemberEntity.memberEntity;
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;

    @Override
    public Optional<List<String>> searchMembers(String name, Long groupId) {
        List<String> result;
        if (groupId == null) {
            result = jpaQueryFactory
                    .select(member.name)
                    .from(member)
                    .where(member.name.contains(name).and(member.mid.ne(CryptUtils.getMid())))
                    .fetch();
        } else {
            result = jpaQueryFactory
                    .select(member.name)
                    .from(member)
                    .innerJoin(groupMemberMapping).on(groupMemberMapping.groupId.ne(groupId))
                    .where(member.name.contains(name).and(member.mid.ne(CryptUtils.getMid())))
                    .groupBy(member.name)
                    .fetch();
        }
        return Optional.ofNullable(result);
    }
}
