package com.teamside.project.alpha.member.domain.mypage.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.member.domain.mypage.model.dto.*;
import com.teamside.project.alpha.member.model.dto.InquiryDto;

import java.util.List;

public interface MyPageService {
    MyPageHome getMyPageHome();

    List<MyGroups> getMyGroups();

    MyReviews getMyReviews(String groupId, Long lastSeq, Long pageSize);

    MyDaily getMyDaily(String groupId, Long lastSeq, Long pageSize);

    MyKeep getKeepMyReviews(Long offset, Long pageSize);

    MyComments getMyComments(String groupId, Long offset, Long pageSize);

    MyKeep getKeepMyDaily(Long offset, Long pageSize);

    MyFollowingDto getMyFollowingList(Long nextOffset, Long pageSize);
    void editKeep(MyKeep.editKeep editKeep) throws CustomException;
    void myInquiry(InquiryDto.MyInquiry myInquiry);
}
