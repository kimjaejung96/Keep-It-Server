package com.teamside.project.alpha.group.domain;

import com.teamside.project.alpha.group.domain.compositeKeys.GroupMemberKeys;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "GROUP_MEMBER_MAPPING")
@IdClass(GroupMemberKeys.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class GroupMemberMappingEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Id
    @Column(name = "GROUP_ID", columnDefinition = "bigint")
    private Long groupId;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID")
    private GroupEntity group;

    @Column(name = "ORD", columnDefinition = "int")
    private Integer ord;

    @Column(name = "FAVORITE", columnDefinition = "boolean")
    private Boolean favorite;

    public GroupMemberMappingEntity(MemberEntity member) {
        this.member = member;
    }

    public GroupMemberMappingEntity(String mid, Long groupId) {
        this.mid = mid;
        this.groupId = groupId;
        this.favorite = false;
    }

    public void updateOrdAndFavorite(Integer ord, Boolean isFavorite) {
        this.ord = ord;
        this.favorite = isFavorite;
    }

    public void updateOrd(Integer ord) {
        this.ord = ord;
    }
}
