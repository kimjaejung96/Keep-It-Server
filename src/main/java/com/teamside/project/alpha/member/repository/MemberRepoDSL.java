package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.domain.mypage.model.dto.MyPageHome;
import com.teamside.project.alpha.member.domain.noti_check.model.entity.dto.NotificationCheckDTO;
import com.teamside.project.alpha.member.model.dto.MemberDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepoDSL {
    Optional<List<MemberDto.InviteMemberList>> searchMembers(String name, String groupId);
    MyPageHome getMyPageHome(List<String> blocks);

    void unfollow(String mid, String targetMid);

    NotificationCheckDTO notiCheck(String mid, LocalDateTime checkActTime, LocalDateTime checkNewsTime);
}
