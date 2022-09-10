package com.teamside.project.alpha.group.domain;

import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "REVIEW")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class DailyEntity {
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

}
