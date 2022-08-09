package com.teamside.project.alpha.member.domain.terms.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "REFRESH_TOKEN")
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

}

