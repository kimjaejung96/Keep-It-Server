package com.teamside.project.alpha.group.repository.dsl;

import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;

import java.util.List;
import java.util.Optional;

public interface GroupRepositoryDSL {
//    void groupNameCheck(String groupName) throws CustomException;

    List<GroupDto.SearchGroupDto> selectGroups(Long groupId, Long pageSize);
    List<GroupDto.MyGroupDto> selectMyGroups(String mId, MyGroupType type);
    List<GroupDto.SearchGroupDto> random();
    Optional<GroupMemberMappingEntity> selectGroupMemberMappingEntity(String mid, Long groupId);
    Optional<GroupMemberMappingEntity> selectLatestFavoriteOrd(String mid);
}
