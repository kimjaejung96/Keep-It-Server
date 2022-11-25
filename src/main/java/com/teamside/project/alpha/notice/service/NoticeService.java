package com.teamside.project.alpha.notice.service;

import com.teamside.project.alpha.notice.model.dto.NoticeDto;

public interface NoticeService {
    NoticeDto getNotices(Long pageSize, Long lastNoticeId);
}
