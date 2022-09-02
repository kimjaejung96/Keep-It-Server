package com.teamside.project.alpha.member.repository.dsl;

import java.util.List;
import java.util.Optional;

public interface MemberRepoDSL {
    Optional<List<String>> searchMembers(String name, Long groupId);
}
