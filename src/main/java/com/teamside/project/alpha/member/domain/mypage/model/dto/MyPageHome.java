package com.teamside.project.alpha.member.domain.mypage.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageHome {
    private MemberDto memberInfo;
    private Long writingCount;
    private Long keepCount;
    private Long followCount;
    @QueryProjection
    public MyPageHome(String memberMid, String memberName, String profileUrl, String phone, Long reviewCount, Long dailyCount, Long reviewKeepCount, Long dailyKeepCount, Long followCount) {
        this.memberInfo = MemberDto.MyPageHome_MemberDto()
                .mid(memberMid)
                .name(memberName)
                .phone(phone)
                .profileUrl(profileUrl)
                .build();
        this.writingCount = reviewCount + dailyCount;
        this.keepCount = reviewKeepCount + dailyKeepCount;
        this.followCount = followCount;
    }
}
