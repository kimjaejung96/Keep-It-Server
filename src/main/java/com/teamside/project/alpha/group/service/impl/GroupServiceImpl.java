package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.constant.GroupConstant;
import com.teamside.project.alpha.group.model.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.GroupService;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;


    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    @Transactional
    public void createGroup(GroupDto group) throws CustomException {
        if (groupRepository.countByMaster(new MemberEntity(CryptUtils.getMid())) >= GroupConstant.GROUP_MAKE_POSSIBLE_COUNT) {
            throw new CustomException(ApiExceptionCode.CAN_NOT_CREATE_GROUP);
        }
        isExistGroupName(group.getName());
        GroupEntity groupEntity = new GroupEntity(group);

        GroupEntity newGroupEntity = groupRepository.save(groupEntity);
        newGroupEntity.addMember(newGroupEntity.getMaster().getMid());
    }

    @Override
    public void updateGroup(GroupDto group) throws CustomException {
        isExistGroupName(group.getName());

        GroupEntity groupEntity = groupRepository.findByGroupId(group.getGroupId()).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        groupEntity.checkGroupMaster(CryptUtils.getMid());

        groupEntity.updateGroup(group);

        groupRepository.save(groupEntity);
    }

    @Override
    public void isExistGroupName(String groupName) throws CustomException {
        if (groupRepository.existsByName(groupName)) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
    }
    @Override
    public void deleteGroup(Long groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkGroupMaster(CryptUtils.getMid());

        groupRepository.delete(group);
    }

    @Override
    public GroupDto.GroupInfoDto selectGroup(Long groupId) throws CustomException {
        return groupRepository.selectGroup(groupId);
    }

    @Override
    public GroupDto.ResponseSearchGroupDto selectGroups(Long lastGroupId, Long pageSize) {
        List<GroupDto.SearchGroupDto> groupList = groupRepository.selectGroups(lastGroupId, pageSize);
        Long responseLastGroupId = groupList.size() == pageSize ? groupList.get((int) (pageSize-1L)).getGroupId() : null;

        return new GroupDto.ResponseSearchGroupDto(null, responseLastGroupId, groupList);
    }

    @Override
    public GroupDto.ResponseSearchGroupDto searchGroup(Long lastGroupId, Long pageSize, String search) {
        List<GroupDto.SearchGroupDto> groupList = groupRepository.searchGroup(lastGroupId, pageSize, search.replaceAll(KeepitConstant.REGEXP_EMOJI, ""));
        Long totalCount = lastGroupId == null ? groupRepository.countByNameContaining(search) : null;
        Long responseLastGroupId = groupList.size() == pageSize ? groupList.get((int) (pageSize-1L)).getGroupId() : null;

        return new GroupDto.ResponseSearchGroupDto(totalCount, responseLastGroupId, groupList);
    }

    @Override
    public List<GroupDto.SearchGroupDto> random() {
        return groupRepository.random();
    }

    @Override
    @Transactional
    public void joinGroup(Long groupId, String password) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkJoinPossible(group, password);

        if (groupRepository.countByGroupMemberMappingEntity(new GroupMemberMappingEntity(new MemberEntity(CryptUtils.getMid()))) >= GroupConstant.MEMBER_JOIN_POSSIBLE_COUNT) {
            throw new CustomException(ApiExceptionCode.CAN_NOT_PARTICIPANT);
        }
        group.addMember(CryptUtils.getMid());

    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.removeMember(CryptUtils.getMid());
    }

    @Override
    public GroupDto.ResponseMyGroupDto selectMyGroups(MyGroupType type) {
        if (type.equals(MyGroupType.NULL)) {
            throw new CustomRuntimeException(ApiExceptionCode.INVALID_GROUP_TYPE);
        }

        String mId = CryptUtils.getMid();
        List<GroupDto.MyGroupDto> myGroups = groupRepository.selectMyGroups(mId, type);

        List<GroupDto.MyGroupDto> favoriteGroups = myGroups.stream()
                .filter(group -> group.getFavorite())
                .sorted(Comparator.comparing(group -> group.getOrd()))
                .collect(Collectors.toList());

        List<GroupDto.MyGroupDto> groupList = type.equals(MyGroupType.FAVORITE) ? null : myGroups.stream()
                .filter(group -> !group.getFavorite())
                .sorted(Comparator.comparing(group -> group.getName()))
                .collect(Collectors.toList());

        GroupDto.ResponseMyGroupDto response = new GroupDto.ResponseMyGroupDto(favoriteGroups, groupList);

        return response;
    }

    @Override
    @Transactional
    public void editFavorite(Long groupId) throws CustomException {
        String mId = CryptUtils.getMid();

        GroupMemberMappingEntity groupMemberMapping = groupRepository.selectGroupMemberMappingEntity(mId, groupId)
                        .orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_MEMBER_NOT_FOUND));

        Boolean isFavorite = !groupMemberMapping.getFavorite();
        Integer ord;

        if (isFavorite) {
            Optional<Integer> ordEntity = groupRepository.selectLatestFavoriteOrd(mId);
            ord = ordEntity.map(o -> o + 1).orElse(1);
        } else {
            ord = null;
        }

        groupMemberMapping.updateOrdAndFavorite(ord, isFavorite);
    }

    @Override
    public void inviteMember(Long groupId, String memberId)  {

    }

    @Override
    @Transactional
    public void updateOrd(GroupDto.RequestUpdateOrdDto request) throws CustomException {
        String mId = CryptUtils.getMid();
        List<GroupMemberMappingEntity> targetList = groupRepository.selectFavoriteMappingGroups(mId);

        validateRequest(request.getGroupList(), targetList);

        targetList.stream().forEach(groupMemberMappingEntity -> {
            for (GroupDto.MyGroupDto dto : request.getGroupList()) {
                if (groupMemberMappingEntity.getGroupId() == dto.getGroupId()) {
                    groupMemberMappingEntity.updateOrd(dto.getOrd());
                }
            }
        });
    }

    public void validateRequest(List<GroupDto.MyGroupDto> request, List<GroupMemberMappingEntity> targetList) throws CustomException {
        if (request.size() != targetList.size()) {
            throw new CustomException(ApiExceptionCode.INVALID_GROUP_REQUEST);
        }

        Integer ordCount = request.stream()
                .map(dto -> dto.getOrd())
                .distinct()
                .collect(Collectors.toList())
                .size();

        if (ordCount != targetList.size()) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_ORD);
        }

        List<Long> ids = request.stream()
                .map(dto -> dto.getGroupId())
                .collect(Collectors.toList());

        List<GroupMemberMappingEntity> filteredList = targetList.stream()
                .filter(target -> !ids.contains(target.getGroupId()))
                .collect(Collectors.toList());

        if (filteredList.size() > 0) {
            throw new CustomException(ApiExceptionCode.GROUP_IS_NOT_MATCH);
        }
    }

    @Override
    public List<GroupDto.SearchGroupDto> statGroups(String referralType, String category) {
        List<GroupDto.SearchGroupDto> groupList;

        if (referralType.equals("RECOM")) {
            groupList = groupRepository.random();
        } else {
            groupList = groupRepository.statGroups(referralType, category);
        }
        return groupList;
    }

    @Override
    public GroupDto.GroupMemberProfileDto groupMemberProfile(Long groupId, String memberId) {
        return groupRepository.groupMemberProfile(groupId, memberId);
    }

    @Override
    public ReviewDto.ResponseSelectReviewsInGroup selectReviewsInGroup(Long groupId, String targetMid, Long pageSize, Long lastReviewId) {
        List<ReviewDto.SelectReviewsInGroup> reviewsInGroup = groupRepository.selectReviewsInGroup(groupId, targetMid, pageSize, lastReviewId);
        Long responseLastGroupId = reviewsInGroup.isEmpty() ? null : reviewsInGroup.get(reviewsInGroup.size()-1).getReview().getReviewId();

        return new ReviewDto.ResponseSelectReviewsInGroup(reviewsInGroup, responseLastGroupId);
    }

    @Override
    public DailyDto.ResponseDailyInGroup selectDailyInGroup(Long groupId, String targetMid, Long pageSize, Long lastDailyId) {
        List<DailyDto.DailyInGroup> dailyInGroup = groupRepository.selectDailyInGroup(groupId, targetMid, pageSize, lastDailyId);
        Long responseLastDailyId = dailyInGroup.isEmpty() ? null : dailyInGroup.get(dailyInGroup.size() - 1).getDailyId();

        return new DailyDto.ResponseDailyInGroup(dailyInGroup, responseLastDailyId);
    }
}
