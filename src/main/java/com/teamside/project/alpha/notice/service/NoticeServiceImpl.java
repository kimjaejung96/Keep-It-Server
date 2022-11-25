package com.teamside.project.alpha.notice.service;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.notice.model.dto.NoticeDto;
import com.teamside.project.alpha.notice.model.entity.NoticeEntity;
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

    @Override
    public NoticeDto.NoticeDetail getNotice(Long noticeId) throws CustomException {
        NoticeEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ApiExceptionCode.NOTICE_NOT_FOUND));

        return new NoticeDto.NoticeDetail(notice);
    }
}
