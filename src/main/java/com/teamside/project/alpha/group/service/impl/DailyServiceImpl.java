package com.teamside.project.alpha.group.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.domain.DailyEntity;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.group.service.DailyService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DailyServiceImpl implements DailyService {
    private final GroupRepository groupRepository;

    public DailyServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public void createDaily(DailyDto.CreateDailyDto dailyDto) throws CustomException {
        GroupEntity groupEntity = groupRepository.findByGroupId(dailyDto.getGroupId()).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        groupEntity.checkExistMember(CryptUtils.getMid());

        groupEntity.createDaily(new DailyEntity(dailyDto));
    }
}
