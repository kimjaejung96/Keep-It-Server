package com.teamside.project.alpha.notice.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "NOTICE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeEntity extends CreateDtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID", columnDefinition = "bigint")
    private Long noticeId;

    @Column(name = "TITLE", columnDefinition = "varchar(100)")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "varchar(500)")
    private String content;

    @Column(name = "TYPE", columnDefinition = "varchar(10)")
    private String type;
}
