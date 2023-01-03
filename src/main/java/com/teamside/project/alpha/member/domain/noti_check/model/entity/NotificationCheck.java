package com.teamside.project.alpha.member.domain.noti_check.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicUpdate
@Table(name = "NOTI_CHECK")
public class NotificationCheck extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @OneToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "ACT_DT", columnDefinition = "DATETIME")
    private LocalDateTime actDt;

    @Column(name = "NEWS_DT", columnDefinition = "DATETIME")
    private LocalDateTime newsDt;

    public NotificationCheck(MemberEntity member) {
        this.member = member;
        this.actDt = LocalDateTime.now();
        this.newsDt = LocalDateTime.now();
    }

    public NotificationCheck() {

    }
}
