package com.teamside.project.alpha.common.aop.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "API_LOG")
public class ApiLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "MID")
    private String mid;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private char status;

    @Column(name = "PROCESS_TIME")
    private float processTime;
}
