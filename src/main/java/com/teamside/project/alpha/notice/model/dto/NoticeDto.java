package com.teamside.project.alpha.notice.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.notice.model.entity.NoticeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDto {
    private Long lastNoticeId;

    private List<Notice> notices;

    public NoticeDto(List<Notice> notices, Long pageSize) {
        this.notices = notices;
        if (notices.size() == pageSize) {
            this.lastNoticeId = notices.get(pageSize.intValue() - 1).getNoticeId();
        } else {
            this.lastNoticeId = null;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Notice {

        private Long noticeId;
        private String title;
        private String createDt;

        @QueryProjection
        public Notice(Long noticeId, String title, String createDt) {
            this.noticeId = noticeId;
            this.title = title;
            this.createDt = createDt;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NoticeDetail {
        private String title;

        private String content;

        private String createDt;

        public NoticeDetail(NoticeEntity noticeEntity) {
            this.title = noticeEntity.getTitle();
            this.content = noticeEntity.getContent();
            this.createDt = noticeEntity.getCreateTime().toString();
        }
    }
}
