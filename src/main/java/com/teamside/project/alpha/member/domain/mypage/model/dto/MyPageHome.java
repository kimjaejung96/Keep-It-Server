package com.teamside.project.alpha.member.domain.mypage.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageHome {
    private String memberMid;
    private String memberName;
    private String profileUrl;
    private Long writingCount;
    private Long keepCount;
    private Long followCount;
    @QueryProjection
    public MyPageHome(String memberMid, String memberName, String profileUrl, Long reviewCount, Long dailyCount, Long reviewKeepCount, Long dailyKeepCount, Long followCount) {
        this.memberMid = memberMid;
        this.memberName = memberName;
        this.profileUrl = profileUrl;
        this.writingCount = reviewCount + dailyCount;
        this.keepCount = reviewKeepCount + dailyKeepCount;
        this.followCount = followCount;
    }
}
