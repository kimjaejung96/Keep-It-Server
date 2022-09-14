package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.DailyEntity;
import com.teamside.project.alpha.group.domain.ReviewEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.member.domain.MemberBlockEntity;
import com.teamside.project.alpha.member.domain.MemberFollowEntity;
import com.teamside.project.alpha.member.domain.RefreshTokenEntity;
import com.teamside.project.alpha.member.domain.TermsEntity;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(name = "MEMBER")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(36)")
    private String mid;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "PHONE", columnDefinition = "char(64)")
    private String phone;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name="FCM_TOKEN", columnDefinition = "varchar(170)")
    private String fcmToken;

    @Column(name="TYPE", columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private SignUpType type;

    @OneToOne(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TermsEntity termsEntity;

    @OneToOne(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RefreshTokenEntity refreshTokenEntity;

    @OneToMany(mappedBy = "master", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupEntity> groupEntities;

    @OneToMany(mappedBy = "master", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntities;

    @OneToMany(mappedBy = "master", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyEntity> dailyEntities;

    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberFollowEntity> memberFollowEntities;

    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberBlockEntity> memberBlockEntities;

    public MemberEntity(String mid) {
        this.mid = mid;
    }

    public MemberEntity(MemberDto.SignUpDto signUpDto) throws CustomException {
        this.mid = UUID.randomUUID().toString();
        this.name = signUpDto.getMember().getName();
        this.phone = CryptUtils.encrypt(signUpDto.getMember().getPhone());
        this.profileUrl = Objects.requireNonNullElse(signUpDto.getMember().getProfileUrl(), "");
        this.fcmToken = Objects.requireNonNullElse(signUpDto.getMember().getFcmToken(), "");
        this.type = Objects.requireNonNullElse(type, SignUpType.PHONE);

        createTerms(signUpDto.getTerms());
    }


    private void createTerms(MemberDto.Terms terms) {
        this.termsEntity = new TermsEntity(this,
                terms.getTerms(),
                terms.getCollect(),
                terms.getGps(),
                terms.getMarketing(),
                terms.getAlarm());
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
