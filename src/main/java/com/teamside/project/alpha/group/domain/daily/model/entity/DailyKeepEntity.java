package com.teamside.project.alpha.group.domain.daily.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "DAILY_KEEP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyKeepEntity extends CreateDtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KEEP_ID", columnDefinition = "bigint")
    private Long keepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DAILY_ID",  referencedColumnName = "DAILY_ID")
    private DailyEntity daily;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER",  referencedColumnName = "MID")
    private MemberEntity member;

    public DailyKeepEntity(Long dailyId, String mid) {
        this.daily = new DailyEntity(dailyId);
        this.member = new MemberEntity(mid);
    }
}
