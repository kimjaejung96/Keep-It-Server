package com.teamside.project.alpha.group.repository.dsl;

import com.teamside.project.alpha.common.exception.CustomException;

public interface GroupRepositoryDSL {
    void groupNameCheck(String groupName, String preName) throws CustomException;
}
