package com.teamside.project.alpha.notice.service;

import com.teamside.project.alpha.notice.model.dto.NoticeDto;
import com.teamside.project.alpha.notice.repository.NoticeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;

    @Override
    public NoticeDto getNotices(Long pageSize, Long lastNoticeId) {
        List<NoticeDto.Notice> notices = noticeRepository.getNotices(pageSize, lastNoticeId);

        return new NoticeDto(notices, pageSize);
    }
}
