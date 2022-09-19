package com.teamside.project.alpha.group.model.domain;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.group.model.domain.compositeKeys.GroupMemberKeys;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "GROUP_KEEP")
@IdClass(GroupMemberKeys.class)
public class GroupKeepEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Id
    @Column(name = "GROUP_ID", columnDefinition = "bigint")
    private Long groupId;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID")
    private GroupEntity group;

}
