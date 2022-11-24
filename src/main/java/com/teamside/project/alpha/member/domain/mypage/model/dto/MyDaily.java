package com.teamside.project.alpha.member.domain.mypage.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyDaily {
    private List<MyDaily.Daily> myDaily;
    private Long lastSeq;
    @Getter
    @NoArgsConstructor
    public static class Daily {
        private Long seq;
        private String title;
        private String groupId;
        private String groupName;
        private String createDt;
        private String dailyId;
        private Long commentCount;
        private String imageUrl;

    }

    public MyDaily(List<MyDaily.Daily> myDaily, Long pageSize) {
        this.myDaily = myDaily;
        if (myDaily.size() == pageSize) {
            this.lastSeq = myDaily.get(pageSize.intValue()-1).getSeq();
        } else {
            this.lastSeq = null;
        }
    }
}
