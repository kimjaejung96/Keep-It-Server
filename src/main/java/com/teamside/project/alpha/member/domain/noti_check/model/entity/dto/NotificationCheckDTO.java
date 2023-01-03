package com.teamside.project.alpha.member.domain.noti_check.model.entity.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class NotificationCheckDTO {
    private Boolean act;
    private Boolean news;
}
