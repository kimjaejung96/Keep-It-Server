package com.teamside.project.alpha.group.model.domain;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "REVIEW_KEEP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class ReviewKeepEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KEEP_ID", columnDefinition = "bigint")
    private Long keepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID",  referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID",  referencedColumnName = "REVIEW_ID")
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER",  referencedColumnName = "MID")
    private MemberEntity member;

}
