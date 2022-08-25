package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.converter.CategoryConverter;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.Category;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "GROUP_LIST")
@NoArgsConstructor
@DynamicUpdate
public class GroupEntity extends TimeEntity {
    @Id
    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

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

    @Convert(converter = CategoryConverter.class)
    @Column(name = "CATEGORY", columnDefinition = "varchar(50)")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER_MID",  referencedColumnName = "MID")
    private MemberEntity master;

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMemberMappingEntity> groupMemberMappingEntity;

    public GroupEntity(GroupDto group) {
        this.groupId = UUID.randomUUID().toString();
        this.name = group.getName();
        this.description = group.getDescription();
        this.password = group.getUsePrivate() ?  group.getPassword() : "";
        this.usePrivate = group.getUsePrivate();
        this.memberQuantity = group.getMemberQuantity();
        this.profileUrl = group.getProfileUrl();
        this.category = group.getCategory();

        this.groupMemberMappingEntity = new ArrayList<>();
        setMasterMember();
        addMember(this.master);
    }

    private void setMasterMember(){
        this.master = new MemberEntity(CryptUtils.getMid());
    }
    private void addMember(MemberEntity member) {

        this.groupMemberMappingEntity.add(
                GroupMemberMappingEntity.builder()
                        .mid(member.getMid())
                        .groupId(this.groupId)
                        .favorite(Boolean.FALSE)
                        .build()
        );
    }

    public void updateGroup(GroupDto group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.password = group.getUsePrivate() ? group.getPassword() : "";
        this.memberQuantity = group.getMemberQuantity();
        this.profileUrl = group.getProfileUrl();
    }
}
