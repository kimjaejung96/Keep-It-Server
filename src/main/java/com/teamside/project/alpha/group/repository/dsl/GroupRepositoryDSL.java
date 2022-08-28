package com.teamside.project.alpha.group.repository.dsl;

import com.teamside.project.alpha.group.model.dto.GroupDto;

import java.util.List;

public interface GroupRepositoryDSL {
//    void groupNameCheck(String groupName) throws CustomException;

    List<GroupDto.SearchGroupDto> selectGroups(Long groupId, Long pageSize);
    List<GroupDto.MyGroupDto> selectMyGroups(String mId);
    List<GroupDto.SearchGroupDto> random();
}
