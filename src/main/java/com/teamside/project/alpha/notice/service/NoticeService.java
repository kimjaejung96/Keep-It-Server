package com.teamside.project.alpha.notice.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.notice.model.dto.NoticeDto;

public interface NoticeService {
    NoticeDto getNotices(Long pageSize, Long lastNoticeId);
    NoticeDto.NoticeDetail getNotice(Long noticeId) throws CustomException;
}
