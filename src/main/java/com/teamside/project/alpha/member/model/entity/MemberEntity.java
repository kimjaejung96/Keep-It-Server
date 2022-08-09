package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.domain.terms.model.entity.RefreshTokenEntity;
import com.teamside.project.alpha.member.domain.terms.model.entity.TermsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEMBER")
public class MemberEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "PHONE", columnDefinition = "char(11)")
    private String phone;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name = "PIN_PROFILE_URL", columnDefinition = "varchar(255)")
    private String pinProfileUrl;

    @Column(name="FCM_TOKEN", columnDefinition = "varchar(170)")
    private String fcmToken;

    @OneToOne(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    private TermsEntity termsEntity;


    @OneToOne(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    private RefreshTokenEntity refreshTokenEntity;

    public void changeTerms(TermsEntity termsEntity) {
        this.termsEntity = termsEntity;
    }

    public void changeRefreshToken(RefreshTokenEntity refreshTokenEntity) {
        this.refreshTokenEntity = refreshTokenEntity;
    }
}
