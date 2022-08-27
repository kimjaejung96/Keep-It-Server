package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.GroupService;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void createGroup(GroupDto group) throws CustomException {
        isExistGroupName(group.getName());
        GroupEntity groupEntity = new GroupEntity(group);

        groupRepository.save(groupEntity);
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
    public void deleteGroup(String groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        creatorCheck(group.getMaster().getMid());

        groupRepository.delete(group);
    }

    @Override
    public GroupDto.SelectGroupDto selectGroup(String groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        return new GroupDto.SelectGroupDto(group);
    }
}
