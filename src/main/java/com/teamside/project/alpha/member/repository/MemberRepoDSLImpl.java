package com.teamside.project.alpha.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.entity.QGroupMemberMappingEntity;
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
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;

    @Override
    public Optional<List<MemberDto.InviteMemberList>> searchMembers(String name, Long groupId) {
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
}
