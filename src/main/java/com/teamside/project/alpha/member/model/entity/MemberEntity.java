package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import com.teamside.project.alpha.member.domain.auth.model.entity.RefreshTokenEntity;
import com.teamside.project.alpha.member.model.dto.AlarmDto;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Table(name = "MEMBER")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends CreateDtEntity {
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

    @Column(name="FCM_TOKEN_LIFE", columnDefinition = "DATETIME")
    private LocalDateTime fcmTokenLife;

    @Column(name="TYPE", columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private SignUpType type;

    @Column(name = "IS_DELETE", columnDefinition = "boolean")
    private Boolean isDelete;
    @Column(name = "UPDATE_DT",nullable = true, columnDefinition = "DATETIME")
    private LocalDateTime updateTime;


    @OneToOne(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    private TermsEntity termsEntity;
    @OneToOne(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    private RefreshTokenEntity refreshTokenEntity;
    @OneToOne(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    private AlarmSetting alarmSetting;
    @OneToMany(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MemberBlockEntity> memberBlockEntities;
    @OneToMany(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GroupMemberMappingEntity> groupMemberMappingEntities;
    public MemberEntity(String mid) {
        this.mid = mid;
    }

    public MemberEntity(MemberDto.SignUpDto signUpDto) throws CustomException {
        this.mid = UUID.randomUUID().toString();
        this.name = signUpDto.getMember().getName();
        this.phone = CryptUtils.encrypt(signUpDto.getMember().getPhone());
        this.profileUrl = Objects.requireNonNullElse(signUpDto.getMember().getProfileUrl(), "");
        this.fcmToken = Objects.requireNonNullElse(signUpDto.getMember().getFcmToken(), "");
        this.fcmTokenLife = LocalDateTime.now();
        this.type = Objects.requireNonNullElse(type, SignUpType.PHONE);
        this.isDelete = false;
        this.alarmSetting = new AlarmSetting(this, true, true, true, true, true, true, true, true);

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
        this.fcmTokenLife = null;
    }


    public void block(String mid, String targetMid) {
        // 이미 차단중이면 해제
        Optional<MemberBlockEntity> blockEntity = this.getMemberBlockEntities().stream()
                .filter(follow -> follow.getMid().equals(mid) && follow.getTargetMid().equals(targetMid))
                .findFirst();

        if (blockEntity.isPresent()) {
            this.memberBlockEntities.remove(blockEntity.get());
        } else {
            this.memberBlockEntities.add(new MemberBlockEntity(mid, targetMid));
        }
    }

    public void deleteMember() {
        this.name = "";
        this.phone = "";
        this.profileUrl = "";
        this.fcmToken = "";
        this.fcmTokenLife = null;
        this.refreshTokenEntity = null;
        this.memberBlockEntities = null;
        this.isDelete = true;
        // status -> true
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
        updateFcmTokenLife();
    }
    public void updateFcmTokenLife() {
        this.fcmTokenLife = LocalDateTime.now();
    }

    public void changeRefreshToken(String refreshToken) {
        this.fcmTokenLife = LocalDateTime.now();
        this.refreshTokenEntity.changeRefreshToken(refreshToken);
    }

    public void updateAlarm(AlarmDto alarmDto) {
        this.alarmSetting.updateAlarm(alarmDto);
    }
}
