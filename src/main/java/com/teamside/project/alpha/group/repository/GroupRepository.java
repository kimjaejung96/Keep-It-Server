package com.teamside.project.alpha.group.repository;

import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository  extends JpaRepository<GroupEntity, Long>, GroupRepositoryDSL{
    boolean existsByName(String name);
    Optional<GroupEntity> findByGroupId(String groupId);
    Long countByGroupMemberMappingEntity(GroupMemberMappingEntity groupMemberMappingEntity);
    Long countByNameContainingAndIsDelete(String search, boolean isDelete);
    Long countByMasterMidAndIsDelete(String memberMid, boolean isDelete);
}
