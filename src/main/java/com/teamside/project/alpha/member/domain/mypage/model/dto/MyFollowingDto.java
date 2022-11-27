package com.teamside.project.alpha.member.domain.mypage.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyFollowingDto {
    private Long nextOffset;
    private List<MyFollowing> myFollowing;
    @Getter
    @NoArgsConstructor
    public static class MyFollowing {
        private String memberName;
        private String profileUrl;
        private String groupName;
        private String memberMid;
        private Boolean isWithdrawal;
    }

    public MyFollowingDto(List<MyFollowing> myFollowing, Long nextOffset, Long pageSize) {
        this.myFollowing = myFollowing;
        if (myFollowing.size() == pageSize) {
            if (nextOffset == null) {
                this.nextOffset = pageSize;
            } else this.nextOffset = nextOffset + pageSize;
        } else {
            this.nextOffset = null;
        }

    }
}
