package com.teamside.project.alpha.notification.service.impl;

import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.MemberRepo;
import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
import com.teamside.project.alpha.notification.model.enumurate.NotificationType;
import com.teamside.project.alpha.notification.repository.NotificationRepository;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MessageSource messageSource;
    private final GroupRepository groupRepository;
    private final MemberRepo memberRepo;
    @Override
    public List<NotificationDto> getNotifications() {
        List<NotificationDto> result;
        List<NotificationEntity> entityList = notificationRepository.findAllByReceiverMidAndNotiDateGreaterThan(CryptUtils.getMid(), LocalDateTime.now().minusWeeks(2));
        result = entityList.parallelStream().map(this::representNotification).collect(Collectors.toList());
        result.sort(Comparator.comparing(NotificationDto::getNotiDate));
        return result;
    }
    @Transactional(readOnly = true)
    public NotificationDto representNotification(NotificationEntity notificationEntity) {
        NotificationDto notificationDto = null;
        GroupEntity group = groupRepository.findByGroupId(notificationEntity.getGroupId()).orElseThrow();
        ReviewEntity review = group.getReviewEntities().stream()
                .filter(d -> notificationEntity.getReviewId().equals(d.getReviewId()))
                .findAny().orElseThrow();
        MemberEntity member = memberRepo.findByMid(notificationEntity.getSenderMid()).orElseThrow();

        switch (notificationEntity.getNotificationType()) {
            case KPS_GD:
                break;
            case KPS_GJ:
                notificationDto = NotificationDto.builder()
                        .notiContent(member.getName()+"님이 그룹에 참여했어요.")
                        .groupId(notificationEntity.getGroupId())
                        .imageUrl(member.getProfileUrl())
                        .notiDate(notificationEntity.getNotiDate().toString())
                        .notificationType(NotificationType.KPS_GJ)
                        .build();
                break;
            case KPS_GNR:
                notificationDto = NotificationDto.builder()
                        .notiContent("리뷰 "+review.getPlace().getPlaceName()+"글이 업데이트 됐어요.")
                        .groupId(notificationEntity.getGroupId())
                        .reviewId(notificationEntity.getReviewId())
                        .imageUrl(group.getProfileUrl())
                        .notiDate(notificationEntity.getNotiDate().toString())
                        .notificationType(NotificationType.KPS_GNR)
                        .build();
                break;
            case KPS_MRK:
                break;
            case KPS_MRC:
                notificationDto = NotificationDto.builder()
                        .notiContent("리뷰 "+review.getPlace().getPlaceName()+"에 댓글이 달렸어요.")
                        .groupId(notificationEntity.getGroupId())
                        .reviewId(notificationEntity.getReviewId())
                        .commentId(notificationEntity.getCommentId())
                        .imageUrl(member.getProfileUrl())
                        .notiDate(notificationEntity.getNotiDate().toString())
                        .notificationType(NotificationType.KPS_MRC)
                        .build();

                break;
            case KPS_MDC:
                break;
            case KPS_MCC:
                break;
            case KPS_MFW:
                break;
            case KPS_FCR:
                break;
            case KPS_GI:
                break;
            case KPS_GE:
                break;
            case KPS_MKT:
                break;
            case KPS_UDT:
                break;
        }

        return notificationDto;
    }
}
