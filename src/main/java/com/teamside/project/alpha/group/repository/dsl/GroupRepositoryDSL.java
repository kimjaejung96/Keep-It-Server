package com.teamside.project.alpha.group.repository.dsl;

import com.teamside.project.alpha.group.model.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;

import java.util.List;
import java.util.Optional;

public interface GroupRepositoryDSL {
//    void groupNameCheck(String groupName) throws CustomException;

    List<GroupDto.SearchGroupDto> searchGroup(Long lastGroupId, Long pageSize, String search);
    List<GroupDto.MyGroupDto> selectMyGroups(String mId, MyGroupType type);
    List<GroupDto.SearchGroupDto> random();
    Optional<GroupMemberMappingEntity> selectGroupMemberMappingEntity(String mid, Long groupId);
    Optional<Integer> selectLatestFavoriteOrd(String mid);
    List<GroupMemberMappingEntity> selectFavoriteMappingGroups(String mid);
    GroupDto.GroupInfoDto selectGroup(Long groupId);
    List<GroupDto.SearchGroupDto> statGroups(String referralType, String category);
    List<GroupDto.SearchGroupDto> selectGroups(Long lastGroupId, Long pageSize);
    GroupDto.GroupMemberProfileDto groupMemberProfile(Long groupId, String memberId);
    List<ReviewDto.SelectReviewsInGroup> selectReviewsInGroup(Long groupId, String targetId, Long pageSize, Long lastReviewId);
    List<DailyDto.DailyInGroup> selectDailyInGroup(Long groupId, String targetId, Long pageSize, Long lastDailyId);
    ReviewDto.ResponseReviewDetail selectReviewDetail(Long reviewId);
}
