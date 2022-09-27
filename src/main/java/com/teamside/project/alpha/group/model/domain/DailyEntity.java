package com.teamside.project.alpha.group.model.domain;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.dto.DailyDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "DAILY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DAILY_ID", columnDefinition = "bigint")
    private Long dailyId;

    @Column(name = "TITLE", columnDefinition = "varchar(50)")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "varchar(2000)")
    private String content;

    @Column(name = "IMAGES", columnDefinition = "varchar(1000)")
    private String images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER",  referencedColumnName = "MID")
    private MemberEntity master;

    public DailyEntity(DailyDto dailyDto) {
        this.title = dailyDto.getTitle();
        this.content = dailyDto.getContent();
        this.images = String.join(",", dailyDto.getImages());
        this.group = new GroupEntity(dailyDto.getGroupId());
        this.master = new MemberEntity(CryptUtils.getMid());
    }
    public void updateDaily(DailyDto.UpdateDailyDto dailyDto) {
        this.title = dailyDto.getTitle();
        this.content = dailyDto.getContent();
        this.images = String.join(",", dailyDto.getImages());
    }

    public void checkDailyMaster(String mid) {
        if (!this.master.getMid().equals(mid)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }
}
