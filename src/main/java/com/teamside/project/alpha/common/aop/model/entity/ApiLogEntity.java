package com.teamside.project.alpha.common.aop.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "API_LOG")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @Column(name = "API_STATUS")
    private String status;

    @Column(name = "API_CODE")
    private int code;

    @Column(name = "PROCESS_TIME")
    private float processTime;

    public ApiLogEntity(String mid, String name, String description, String status, float processTime, int code) {
        this.mid = mid;
        this.name = name;
        if (description.length() > 5000) {
            this.description = description.substring(0, 5000);
        } else this.description = description;
        this.status = status;
        this.processTime = processTime;
        this.code = code;
    }
}
