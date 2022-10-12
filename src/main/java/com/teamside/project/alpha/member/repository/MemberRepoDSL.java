package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.dto.MemberDto;

import java.util.List;
import java.util.Optional;

public interface MemberRepoDSL {
    Optional<List<MemberDto.InviteMemberList>> searchMembers(String name, Long groupId);
}
