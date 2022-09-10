package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.constant.GroupConstant;
import com.teamside.project.alpha.group.model.dto.GroupDto;
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
        newGroupEntity.addMember(newGroupEntity.getMaster());
    }

    @Override
    public void updateGroup(GroupDto group) throws CustomException {
        isExistGroupName(group.getName());

        GroupEntity groupEntity = groupRepository.findByGroupId(group.getGroupId()).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        creatorCheck(groupEntity.getMaster().getMid());

        groupEntity.updateGroup(group);

        groupRepository.save(groupEntity);
    }

    @Override
    public void isExistGroupName(String groupName) throws CustomException {
        if (groupRepository.existsByName(groupName)) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
    }
    private void creatorCheck(String mid) throws CustomException {
        if (!CryptUtils.getMid().equals(mid)) {
            throw new CustomException(ApiExceptionCode.FORBIDDEN);
        }
    }

    @Override
    public void deleteGroup(Long groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        creatorCheck(group.getMaster().getMid());

        groupRepository.delete(group);
    }

    @Override
    public GroupDto.SelectGroupDto selectGroup(Long groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        return new GroupDto.SelectGroupDto(group);
    }

    @Override
    public List<GroupDto.SearchGroupDto> selectGroups(Long lastGroupId, Long pageSize) {
        return groupRepository.selectGroups(lastGroupId, pageSize, null);
    }

    @Override
    public GroupDto.ResponseSearchGroupDto searchGroup(Long lastGroupId, Long pageSize, String search) {
        List<GroupDto.SearchGroupDto> groupList = groupRepository.selectGroups(lastGroupId, pageSize, search.replaceAll(KeepitConstant.REGEXP_EMOJI, ""));
        Long totalCount = lastGroupId == null ? groupRepository.countByNameContaining(search) : null;

        return new GroupDto.ResponseSearchGroupDto(totalCount, groupList);
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
        group.addMember(new MemberEntity(CryptUtils.getMid()));

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
}
