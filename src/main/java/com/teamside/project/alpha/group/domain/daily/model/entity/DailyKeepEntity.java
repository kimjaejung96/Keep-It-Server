package com.teamside.project.alpha.group.domain.daily.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
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

    @Column(name = "MEMBER", columnDefinition = "char(36)")
    private String memberMid;

    public DailyKeepEntity(Long dailyId, String mid) {
        this.daily = new DailyEntity(dailyId);
        this.memberMid = mid;
    }
}
