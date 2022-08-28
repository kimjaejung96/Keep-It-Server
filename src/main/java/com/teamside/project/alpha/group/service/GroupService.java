package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.GroupDto;

import java.util.List;

public interface GroupService {
    void createGroup(GroupDto group) throws CustomException;
    void updateGroup(GroupDto group) throws CustomException;
    void isExistGroupName(String groupName) throws CustomException;
    void deleteGroup(Long groupId) throws CustomException;
    GroupDto.SelectGroupDto selectGroup(Long groupId) throws CustomException;
    List<GroupDto.SearchGroupDto> selectGroups(Long groupId, Long pageSize);
    List<GroupDto.SearchGroupDto> random();
    void joinGroup(Long groupId, String password) throws CustomException;
    List<GroupDto.MyGroupDto> selectMyGroups();
}
