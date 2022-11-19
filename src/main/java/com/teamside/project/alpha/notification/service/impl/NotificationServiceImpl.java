package com.teamside.project.alpha.notification.service.impl;

import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.model.entity.NotificationEntity;
import com.teamside.project.alpha.notification.repository.NotificationRepository;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    @Override
    public List<NotificationDto> getNotifications() {
        List<NotificationDto> result = null;
        List<NotificationEntity> entityList = notificationRepository.getNotifications();
        return result;
    }
//    public NotificationDto representNotification(NotificationEntity notificationEntity) {
//        NotificationDto notificationDto = null;
//        Optional<ReviewEntity> review = Optional.empty();
//        Optional<GroupEntity> group;
//        Optional<MemberEntity> member;
//        group = groupRepository.findByGroupId(notificationEntity.getGroupId());
//        if (group.isPresent()) {
//            review = group.get().getReviewEntities().stream()
//                    .filter(d -> notificationEntity.getReviewId().equals(d.getReviewId()))
//                    .findAny();
//        }
//
//        member = memberRepo.findByMid(notificationEntity.getSenderMid());
//
//        switch (notificationEntity.getNotificationType()) {
//            case KPS_GD:
//                break;
//            case KPS_GJ:
//                notificationDto = NotificationDto.builder()
//                        .notiContent(member.get().getName()+"님이 그룹에 참여했어요.")
//                        .groupId(notificationEntity.getGroupId())
//                        .imageUrl(member.get().getProfileUrl())
//                        .notiDate(notificationEntity.getNotiDate().toString())
//                        .notificationType(NotificationType.KPS_GJ)
//                        .build();
//                break;
//            case KPS_GNR:
//                notificationDto = NotificationDto.builder()
//                        .notiContent("리뷰 "+review.get().getPlace().getPlaceName()+"글이 업데이트 됐어요.")
//                        .groupId(notificationEntity.getGroupId())
//                        .reviewId(notificationEntity.getReviewId())
//                        .imageUrl(group.get().getProfileUrl())
//                        .notiDate(notificationEntity.getNotiDate().toString())
//                        .notificationType(NotificationType.KPS_GNR)
//                        .build();
//                break;
//            case KPS_MRK:
//                break;
//            case KPS_MRC:
//                notificationDto = NotificationDto.builder()
//                        .notiContent("리뷰 "+ review.get().getPlace().getPlaceName()+"에 댓글이 달렸어요.")
//                        .groupId(notificationEntity.getGroupId())
//                        .reviewId(notificationEntity.getReviewId())
//                        .commentId(notificationEntity.getCommentId())
//                        .imageUrl(group.get().getProfileUrl())
//                        .notiDate(notificationEntity.getNotiDate().toString())
//                        .notificationType(NotificationType.KPS_MRC)
//                        .build();
//
//                break;
//            case KPS_MDC:
//                break;
//            case KPS_MCC:
//                break;
//            case KPS_MFW:
//                break;
//            case KPS_FCR:
//                break;
//            case KPS_GI:
//                break;
//            case KPS_GE:
//                break;
//            case KPS_MKT:
//                break;
//            case KPS_UDT:
//                break;
//        }
//
//        return notificationDto;
//    }
}
