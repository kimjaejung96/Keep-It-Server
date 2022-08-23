package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.enumurate.Category;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "GROUP_LIST")
@NoArgsConstructor
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

    @Column(name = "USE_PRIVATE", columnDefinition = "boolean")
    private Boolean usePrivate;

    @Column(name = "MEMBER_QUANTITY", columnDefinition = "int")
    private Integer memberQuantity;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name = "CATEGORY", columnDefinition = "varchar(50)")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER_MID",  referencedColumnName = "MID")
    private MemberEntity master;

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMemberMappingEntity> groupMemberMappingEntity;

    public GroupEntity(String name, String description, String password, Boolean usePrivate, Integer memberQuantity, String profileUrl, Category category) {
        this.name = name;
        this.description = description;
        this.password = password;
        this.usePrivate = usePrivate;
        this.memberQuantity = memberQuantity;
        this.profileUrl = profileUrl;
        this.category = category;
    }

    public void setMasterMember(MemberEntity master){
        this.master = master;
    }
}
