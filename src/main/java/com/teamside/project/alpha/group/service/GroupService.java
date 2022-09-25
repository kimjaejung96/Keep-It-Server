package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;

import java.util.List;

public interface GroupService {
    void createGroup(GroupDto group) throws CustomException;
    void updateGroup(GroupDto group) throws CustomException;
    void isExistGroupName(String groupName) throws CustomException;
    void deleteGroup(Long groupId) throws CustomException;
    GroupDto.GroupInfoDto selectGroup(Long groupId) throws CustomException;
    List<GroupDto.SearchGroupDto> selectGroups(Long groupId, Long pageSize);
    List<GroupDto.SearchGroupDto> random();
    void joinGroup(Long groupId, String password) throws CustomException;
    void leaveGroup(Long groupId) throws CustomException;
    GroupDto.ResponseMyGroupDto selectMyGroups(MyGroupType type);
    void editFavorite(Long groupId) throws CustomException;
    void inviteMember(Long groupId, String memberId) throws CustomException;
    GroupDto.ResponseSearchGroupDto searchGroup(Long lastGroupId, Long pageSize, String search);
    void updateOrd(GroupDto.RequestUpdateOrdDto request) throws CustomException;
    List<GroupDto.SearchGroupDto> statGroups(String referralType, String category);
}
