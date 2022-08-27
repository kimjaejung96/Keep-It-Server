package com.teamside.project.alpha.group.repository;

import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.dsl.GroupRepositoryDSL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository  extends JpaRepository<GroupEntity, Long>, GroupRepositoryDSL{
    boolean existsByName(String name);
    Optional<GroupEntity> findByGroupId(Long groupId);
}
