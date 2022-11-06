package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;

import java.util.List;

public interface GroupService {
    String createGroup(GroupDto group) throws CustomException;
    void updateGroup(String groupId, GroupDto groupDto) throws CustomException;
    void isExistGroupName(String groupName) throws CustomException;
    void deleteGroup(String groupId) throws CustomException;
    GroupDto.GroupInfoDto selectGroup(String groupId) throws CustomException;
    GroupDto.ResponseSearchGroupDto selectGroups(Long lastGroupId, Long pageSize);
    List<GroupDto.SearchGroupDto> random();
    void joinGroup(String groupId, String password) throws CustomException;
    void leaveGroup(String groupId) throws CustomException;
    GroupDto.ResponseMyGroupDto selectMyGroups(MyGroupType type);
    void editFavorite(String groupId) throws CustomException;
    void inviteMember(String groupId, String memberId) throws CustomException;
    GroupDto.ResponseSearchGroupDto searchGroup(Long lastGroupSeq, Long pageSize, String search);
    void updateOrd(GroupDto.RequestUpdateOrdDto request) throws CustomException;
    List<GroupDto.SearchGroupDto> statGroups(String referralType, String category);
    GroupDto.GroupMemberProfileDto groupMemberProfile(String groupId, String memberId);
    ReviewDto.ResponseSelectReviewsInGroup selectReviewsInGroup(String groupId, String targetMid, Long pageSize, Long lastReviewId);

    Object selectDailyInGroup(String groupId, String targetMid, Long pageSize, Long lastDailyId);

    GroupDto.GroupHome selectGroupHome(String groupId);
    void follow(String groupId, String targetMid) throws CustomException;

    void exileMember(String groupId, String memberId);

    List<GroupDto.GroupAlarmSetting> selectGroupReviewAlarm(String alarmType) throws CustomException;

    void updateGroupAlarm(GroupDto.GroupAlarmSetting groupAlarmSetting) throws CustomException;
}
