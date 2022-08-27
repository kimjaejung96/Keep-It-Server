package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.GroupDto;

public interface GroupService {
    void createGroup(GroupDto group) throws CustomException;
    void updateGroup(GroupDto group) throws CustomException;
    void isExistGroupName(String groupName) throws CustomException;
    void deleteGroup(String groupId) throws CustomException;
    GroupDto.SelectGroupDto selectGroup(String groupId) throws CustomException;
}
