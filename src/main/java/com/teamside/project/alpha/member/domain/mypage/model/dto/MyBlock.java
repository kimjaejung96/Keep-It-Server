package com.teamside.project.alpha.member.domain.mypage.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyBlock {
    private Long nextOffset;

    private List<Block> blocks;

    @Getter
    @NoArgsConstructor
    public static class Block {
        private String memberMid;
        private String memberName;
        private String profileUrl;
        private String groupName;
        private Boolean isWithdrawal;

        @QueryProjection
        public Block(String memberMid, String memberName, String profileUrl, String groupName, Boolean isWithdrawal) {
            this.memberMid = memberMid;
            this.memberName = memberName;
            this.profileUrl = profileUrl;
            this.groupName = groupName;
            this.isWithdrawal = isWithdrawal;
        }
    }

    public MyBlock(List<Block> blocks, Long nextOffset, Long pageSize) {
        this.blocks = blocks;
        if (blocks.size() == pageSize) {
            if (nextOffset == null) {
                this.nextOffset = pageSize;
            } else {
                this.nextOffset = nextOffset + pageSize;
            }
        } else {
            this.nextOffset = null;
        }
    }
}
