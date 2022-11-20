package com.teamside.project.alpha.member.domain.mypage.service.impl;

import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyGroups;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyPageHome;
import com.teamside.project.alpha.member.domain.mypage.model.dto.MyReviews;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {
    private final MemberRepo memberRepo;
    private final GroupRepository groupRepository;
    @Override
    public MyPageHome getMyPageHome() {
        return memberRepo.getMyPageHome();
    }

    @Override
    public List<MyGroups> getMyGroups() {
        return groupRepository.getMyGroups();
    }

    @Override
    public MyReviews getMyReviews(String groupId, Long lastSeq, Long pageSize) {
        List<MyReviews.Reviews> myReviews =  groupRepository.getMyReviews(groupId, lastSeq, pageSize);
        myReviews.stream().filter(d -> d.getImageUrl() != null).forEach(d -> d.splitImages(d.getImageUrl()));

        return new MyReviews(myReviews, pageSize);
    }
}
