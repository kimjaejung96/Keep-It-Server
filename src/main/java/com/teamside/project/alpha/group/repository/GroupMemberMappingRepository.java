package com.teamside.project.alpha.group.repository;

import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberMappingRepository extends JpaRepository<GroupMemberMappingEntity, Long> {

    Optional<GroupMemberMappingEntity> findByMidAndGroupId(String mid, Long groupId);

    GroupMemberMappingEntity findTop1ByMidAndFavoriteOrderByOrdDesc(String mid, Boolean favorite);

}
