package com.teamside.project.alpha.group.domain.daily.model.entity;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Table(name = "DAILY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class DailyEntity extends TimeEntity {
    @Id
    @Column(name = "DAILY_ID", columnDefinition = "char(36)")
    private String dailyId;

    @Column(name = "SEQ", columnDefinition = "BIGINT(20) NOT NULL UNIQUE KEY auto_increment")
    private Long seq;

    @Column(name = "TITLE", columnDefinition = "varchar(50)")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "varchar(2000)")
    private String content;

    @Column(name = "IMAGE", columnDefinition = "varchar(1000)")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @Column(name = "MASTER", columnDefinition = "char(36)")
    private String masterMid;

    @OneToMany(mappedBy = "daily", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyCommentEntity> dailyCommentEntities;

    @OneToMany(mappedBy = "daily", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyKeepEntity> dailyKeepEntities;

    public DailyEntity(String groupId, DailyDto dailyDto) {
        this.dailyId = UUID.randomUUID().toString();
        this.title = dailyDto.getTitle();
        this.content = dailyDto.getContent();
        this.image = dailyDto.getImage();
        this.group = new GroupEntity(groupId);
        this.masterMid = CryptUtils.getMid();
    }

    public DailyEntity(String dailyId) {
        this.dailyId = dailyId;
    }
    public void updateDaily(DailyDto.UpdateDailyDto dailyDto) {
        this.title = dailyDto.getTitle();
        this.content = dailyDto.getContent();
        this.image = dailyDto.getImage();
    }

    public void checkDailyMaster(String mid) {
        if (!this.masterMid.equals(mid)) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void createComment(CommentDto.CreateComment comment, String dailyId) {
        this.dailyCommentEntities.add(DailyCommentEntity.createComment(comment, dailyId));
    }

    public void keepDaily(String dailyId, String mid) {
        Optional<DailyKeepEntity> dailyEntity = this.dailyKeepEntities.stream()
                .filter(daily -> (daily.getMemberMid().equals(mid) && daily.getDaily().getDailyId().equals(dailyId)))
                .findFirst();

        if (dailyEntity.isPresent()) {
            this.getDailyKeepEntities().remove(dailyEntity.get());
        } else {
            this.getDailyKeepEntities().add(new DailyKeepEntity(dailyId, mid));
        }
    }
}
