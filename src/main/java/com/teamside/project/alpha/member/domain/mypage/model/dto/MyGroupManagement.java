package com.teamside.project.alpha.member.domain.mypage.model.dto;

import com.teamside.project.alpha.group.model.enumurate.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@Getter
public class MyGroupManagement {
    private Integer groupCount;
    private List<Group> groups;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Group {
        private String groupId;
        private String groupName;
        private Category category;
        private Boolean isDelete;
        private String deleteDt;
        private String exitDt;
        private Boolean existReview;
        private Boolean existDaily;
        private Boolean existReviewComment;
        private Boolean existDailyComment;

        public void changeDeleteDt() {
            if (this.isDelete) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime deleteDt = LocalDateTime.parse(this.deleteDt, formatter);

                this.deleteDt = deleteDt.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
        }
    }

    public MyGroupManagement(List<Group> groups) {
        this.groupCount = groups.size();
        this.groups = groups;
    }
}
