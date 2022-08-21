package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.group.domain.GroupMemberMapping;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "GROUP_LIST")
public class GroupEntity extends TimeEntity {
    @Id
    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private final String groupId = UUID.randomUUID().toString();

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "varchar(200)")
    private String description;

    @Column(name = "PASSWORD", columnDefinition = "varchar(8)")
    private String password;

    @Column(name = "IS_PRIVATE", columnDefinition = "boolean")
    private Boolean isPrivate;

    @Column(name = "MEMBER_QUANTITY", columnDefinition = "int")
    private Integer memberQuantity;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name = "CATEGORY", columnDefinition = "varchar(50)")
    private String category;

    @ManyToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity master;

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMemberMapping> groupMemberMapping;

}
