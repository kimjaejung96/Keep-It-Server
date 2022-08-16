package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "IS_PRIVATE")
    private Boolean isPrivate;

    @Column(name = "MEMBER_QUANTITY")
    private Long memberQuantity;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name = "CATEGORY", columnDefinition = "varchar(50)")
    private String category;

}
