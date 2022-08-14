package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "INQUIRY")
@Getter
public class InquiryEntity extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ", columnDefinition = "bigint")
    private String seq;

    @Column(name = "EMAIL", columnDefinition = "varchar(100)")
    private String email;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "PLACE", columnDefinition = "varchar(100)")
    private String place;

    @Column(name = "WORLD", columnDefinition = "varchar(100)")
    private String world;

    @Column(name = "ETC", columnDefinition = "varchar(200)")
    private String etc;
    public InquiryEntity(String email, String name, String place, String world, String etc) {
        this.email = email;
        this.name = name;
        this.place = place;
        this.world = world;
        this.etc = etc;
    }

    public InquiryEntity() {

    }
}
