package com.teamside.project.alpha.member.domain;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "REFRESH_TOKEN")
public class RefreshTokenEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @OneToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "TOKEN")
    private String refreshToken;

    public RefreshTokenEntity(MemberEntity member, String refreshToken) {
        this.member = member;
        this.refreshToken = refreshToken;
    }

    public RefreshTokenEntity() {

    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

