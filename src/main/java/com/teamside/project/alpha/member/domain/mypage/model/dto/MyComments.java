package com.teamside.project.alpha.member.domain.mypage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyComments {
    private List<comments> myComments;

    public MyComments(List<comments> myComments) {
        this.myComments = myComments;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class comments {
        private String viewType;

        private String viewId;

        private String comment;

        private String groupId;

        private String groupName;

        private String title;

        private String creatDt;

        private String status;

        private Boolean viewIsDelete;
    }
}
