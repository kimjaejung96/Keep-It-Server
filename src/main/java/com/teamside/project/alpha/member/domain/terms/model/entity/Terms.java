package com.teamside.project.alpha.member.domain.terms.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Terms {
    @Id
    @Column(name = "MID")
    private String mid;
    @Column(name = "TERMS")
    private char terms;
    @Column(name = "COLLECT")
    private char collect;
    @Column(name = "MARKETING")
    private char marketing;



}
