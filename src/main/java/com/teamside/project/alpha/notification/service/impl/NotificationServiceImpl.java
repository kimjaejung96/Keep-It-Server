package com.teamside.project.alpha.notification.service.impl;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.repository.NotificationRepository;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepo memberRepo;

    @Override
    @Transactional
    public NotificationDto.MyNotifications getNotifications(String type, Long pageSize, Long nextOffset) throws CustomException {
        String mid = CryptUtils.getMid();
        MemberEntity member = memberRepo.findByMid(mid)
                .orElseThrow(() -> new CustomException(ApiExceptionCode.MEMBER_NOT_FOUND));

        List<NotificationDto> notifications;
        // ACT(활동), NEWS(소식)
        if (type.equals("ACT")) {
            notifications = notificationRepository.getNotifications(pageSize, nextOffset);
        } else {
            notifications = notificationRepository.getActNotifications(pageSize, nextOffset);
        }
        notifications.forEach(data -> data.createMsg());

        member.getNotiCheck().updateDt(type);

        return new NotificationDto.MyNotifications(notifications, nextOffset, pageSize);
    }

}
