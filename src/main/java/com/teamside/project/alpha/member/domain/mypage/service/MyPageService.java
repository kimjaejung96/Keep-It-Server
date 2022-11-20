package com.teamside.project.alpha.member.domain.mypage.service;

import com.teamside.project.alpha.member.domain.mypage.model.dto.MyGroups;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyPageHome;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyReviews;

import java.util.List;

public interface MyPageService {
    MyPageHome getMyPageHome();

    List<MyGroups> getMyGroups();

    MyReviews getMyReviews(String groupId, Long lastSeq, Long pageSize);
}
