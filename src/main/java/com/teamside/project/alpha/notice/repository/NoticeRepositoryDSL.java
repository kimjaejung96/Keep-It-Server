package com.teamside.project.alpha.notice.repository;

import com.teamside.project.alpha.notice.model.dto.NoticeDto;

import java.util.List;

public interface NoticeRepositoryDSL {

    List<NoticeDto.Notice> getNotices(Long pageSize, Long lastNoticeId);

}
