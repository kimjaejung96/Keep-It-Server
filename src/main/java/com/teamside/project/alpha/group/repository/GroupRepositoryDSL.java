package com.teamside.project.alpha.group.repository;

import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.member.domain.mypage.model.dto.*;

import java.util.List;
import java.util.Optional;

public interface GroupRepositoryDSL {
//    void groupNameCheck(String groupName) throws CustomException;

    List<GroupDto.SearchGroupDto> searchGroup(Long lastGroupSeq, Long pageSize, String search);
    List<GroupDto.MyGroupDto> selectMyGroups(String mId, MyGroupType type);
    List<GroupDto.SearchGroupDto> random();
    Optional<GroupMemberMappingEntity> selectGroupMemberMappingEntity(String mid, String groupId);
    Optional<Integer> selectLatestFavoriteOrd(String mid);
    List<GroupMemberMappingEntity> selectFavoriteMappingGroups(String mid);
    GroupDto.GroupInfoDto selectGroup(String groupId);
    List<GroupDto.SearchGroupDto> statGroups(String referralType, String category);
    List<GroupDto.SearchGroupDto> selectGroups(Long lastGroupId, Long pageSize);
    GroupDto.GroupMemberProfileDto groupMemberProfile(String groupId, String memberId);
    List<ReviewDto.SelectReviewsInGroup> selectReviewsInGroup(String groupId, String targetId, Long pageSize, Long lastReviewSeq);
    List<DailyDto.DailyInGroup> selectDailyInGroup(String groupId, String targetId, Long pageSize, Long lastDailySeq);
    ReviewDto.ResponseReviewDetail selectReviewDetail(String groupId, String reviewId);
    DailyDto.ResponseDailyDetail selectDaily(String groupId, String dailyId);
    long countJoinGroup(String mid);

    List<GroupDto.GroupAlarmSetting> selectGroupAlarm(String alarmType);

    List<GroupDto.MyFollow> selectMyFollow();
    CommentDto.CommentDetail getReviewCommentDetail(String groupId, String reviewId, Integer nextOffset, int limit);
    List<MyGroups> getMyGroups();
    List<MyReviews.Reviews> getMyReviews(String groupId, Long lastSeq, Long pageSize);
    List<MyDaily.Daily> getMyDaily(String groupId, Long lastSeq, Long pageSize);
    List<MyKeep.KeepReview> getKeepMyReviews(Long offset, Long pageSize);
    List<MyKeep.KeepDaily> getKeepMyDaily(Long offset, Long pageSize);

    List<MyComments.comments> getMyComments(String groupId, Long offset, Long pageSize);
    MyFollowingDto getMyFollowingDto(Long nextOffSet, Long pageSize);
}
