package com.teamside.project.alpha.group.domain;

import com.teamside.project.alpha.group.domain.compositeKeys.GroupMemberMappingKeys;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "GROUP_MEMBER_MAPPING")
@IdClass(GroupMemberMappingKeys.class)
public class GroupMemberMapping {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Id
    @Column(name = "GROUP_ID", columnDefinition = "char(16)")
    private String groupId;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @Column(name = "ORD", columnDefinition = "int")
    private Integer ord;

    @Column(name = "IS_FAVORITE", columnDefinition = "boolean")
    private Boolean isFavorite;

}
