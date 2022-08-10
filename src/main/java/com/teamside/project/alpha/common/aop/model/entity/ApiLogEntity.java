package com.teamside.project.alpha.common.aop.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "API_LOG")
@NoArgsConstructor
public class ApiLogEntity extends CreateDtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "MID")
    private String mid;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(5000)")
    private String description;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PROCESS_TIME")
    private float processTime;

    public ApiLogEntity(String mid, String name, String description, String status, float processTime) {
        this.mid = mid;
        this.name = name;
        this.description = description;
        this.status = status;
        this.processTime = processTime;
    }
}
