package com.teamside.project.alpha.group.repository;

import com.teamside.project.alpha.group.model.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    boolean existsByName(String name);
}
