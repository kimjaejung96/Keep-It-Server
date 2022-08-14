package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.domain.terms.model.entity.RefreshTokenEntity;
import com.teamside.project.alpha.member.domain.terms.model.entity.TermsEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Table(name = "MEMBER")
public class MemberEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "PHONE", columnDefinition = "char(24)")
    private String phone;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name = "PIN_PROFILE_URL", columnDefinition = "varchar(255)")
    private String pinProfileUrl;

    @Column(name="FCM_TOKEN", columnDefinition = "varchar(170)")
    private String fcmToken;

    @Column(name="TYPE", columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private SignUpType type;

    @OneToOne(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TermsEntity termsEntity;


    @OneToOne(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RefreshTokenEntity refreshTokenEntity;

    public MemberEntity(String mid, String name, String phone, String profileUrl, String pinProfileUrl, String fcmToken, SignUpType type) {
        this.mid = mid;
        this.name = name;
        this.phone = phone;
        this.profileUrl = Objects.requireNonNullElse(profileUrl, "");
        this.pinProfileUrl = Objects.requireNonNullElse(pinProfileUrl, "");
        this.fcmToken = Objects.requireNonNullElse(fcmToken, "");
        this.type = Objects.requireNonNullElse(type, SignUpType.PHONE);
    }


    public MemberEntity() {

    }

    public void createTerms(TermsEntity termsEntity) {
        this.termsEntity = termsEntity;
    }

    public void createRefreshToken(String refreshToken) {
        this.refreshTokenEntity = new RefreshTokenEntity(this, refreshToken);
    }

    public void logout() {
        deleteRefreshToken();
        deleteFcmToken();
    }
    private void deleteRefreshToken() {
        this.refreshTokenEntity.changeRefreshToken("");
    }
    private void deleteFcmToken() {
        this.fcmToken = "";
    }


}
