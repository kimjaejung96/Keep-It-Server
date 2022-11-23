package com.teamside.project.alpha.member.domain.mypage.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.domain.mypage.model.dto.*;
import com.teamside.project.alpha.member.domain.mypage.service.MyPageService;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public MyDaily getMyDaily(String groupId, Long lastSeq, Long pageSize) {
        List<MyDaily.Daily> myDaily = groupRepository.getMyDaily(groupId, lastSeq, pageSize);

        return new MyDaily(myDaily, pageSize);
    }

    @Override
    public MyKeep getKeepMyReviews(Long offset, Long pageSize) {
        List<MyKeep.KeepReview> reviews = groupRepository.getKeepMyReviews(offset, pageSize);
        reviews.stream().filter(d -> d.getImageUrl() != null).forEach(d -> d.splitImages(d.getImageUrl()));

        return new MyKeep(reviews, null);
    }

    @Override
    public MyKeep getKeepMyDaily(Long offset, Long pageSize) {
        List<MyKeep.KeepDaily> keepDaily = groupRepository.getKeepMyDaily(offset, pageSize);

        return new MyKeep(null, keepDaily);
    }

    @Override
    public MyComments getMyComments(String groupId, Long offset, Long pageSize) {
        List<MyComments.comments> myComments = groupRepository.getMyComments(groupId, offset, pageSize);

        return new MyComments(myComments);
    }

    @Override
    @Transactional
    public void editKeep(MyKeep.editKeep editKeep) throws CustomException {
        String type = editKeep.getType() == null ? "" : editKeep.getType();

        if (type.equals("REVIEW")) {
            groupRepository.editReviewKeep(editKeep);
        } else if (type.equals("DAILY")) {
            groupRepository.editDailyKeep(editKeep);
        } else {
            throw new CustomException(ApiExceptionCode.INVALID_VIEW_TYPE);
        }
    }
}
