package com.teamside.project.alpha.common.forbidden.model.entity;

import com.teamside.project.alpha.common.forbidden.model.enumutrate.ForbiddenWordType;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "FORBIDDEN_WORD")
public class ForbiddenWordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "bigint(20)")
    private String id;
    @Column(name = "WORD", columnDefinition = "varchar(100)")
    private String word;
    @Column(name = "TYPE", columnDefinition = "varchar(100)")
    @Enumerated(EnumType.STRING)
    private ForbiddenWordType type;
}
