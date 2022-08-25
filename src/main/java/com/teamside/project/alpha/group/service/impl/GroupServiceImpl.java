package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
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
        if (groupRepository.existsByName(group.getName())) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }

        GroupEntity groupEntity = new GroupEntity(group);

        groupRepository.save(groupEntity);
    }

    @Override
    public void updateGroup(GroupDto group) throws CustomException {
        if (groupRepository.existsByName(group.getName())) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }

    }
}
