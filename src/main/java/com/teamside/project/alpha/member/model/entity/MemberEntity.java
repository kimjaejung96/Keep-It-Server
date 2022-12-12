package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
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
import java.util.stream.Collectors;

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
                terms.getMarketing());
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


    public Boolean block(String mid, String targetMid, String groupId) {
        // 이미 차단중이면 해제
        Optional<MemberBlockEntity> blockEntity = this.getMemberBlockEntities().stream()
                .filter(follow -> follow.getMid().equals(mid) && follow.getTargetMid().equals(targetMid))
                .findAny();

        if (blockEntity.isPresent()) {
            this.memberBlockEntities.remove(blockEntity.get());
            return false;
        } else {
            if (groupId == null) {
                throw new CustomRuntimeException(ApiExceptionCode.BAD_REQUEST);
            } else {
                // 팔로우 존재 시 > 그룹별 팔로우 전부 끊기
                this.memberBlockEntities.add(new MemberBlockEntity(mid, targetMid, groupId));
                return true;
            }
        }
    }

    public void withdrawalMember() {
        this.phone = "";
        this.fcmToken = "";
        this.fcmTokenLife = null;
        this.refreshTokenEntity = null;
        this.memberBlockEntities.clear();
        this.groupMemberMappingEntities.stream()
                .filter(d -> !d.getGroup().getMasterMid().equals(CryptUtils.getMid()))
                .forEach(d -> d.updateStatus(GroupMemberStatus.WITHDRAWAL));
        this.isDelete = true;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
        updateFcmTokenLife();
    }
    public void updateFcmTokenLife() {
        if (this.fcmToken.isBlank()) {
            this.fcmTokenLife = null;
        } else {
            this.fcmTokenLife = LocalDateTime.now();
        }
    }

    public void changeRefreshToken(String refreshToken) {
        this.fcmTokenLife = LocalDateTime.now();
        this.refreshTokenEntity.changeRefreshToken(refreshToken);
    }

    public void updateAlarm(AlarmDto alarmDto) {
        this.alarmSetting.updateAlarm(alarmDto);
    }

    public void checkWithdrawal() {
        if (this.isDelete) {
            throw new CustomRuntimeException(ApiExceptionCode.MEMBER_IS_WITHDRAWAL);
        }
    }

    public void updateTerms(AlarmDto alarmDto) {
        this.termsEntity.updateTerms(alarmDto);
    }

    public void updateMember(MemberDto.UpdateMember updateMember) {
        this.name = updateMember.getName();
        this.profileUrl = updateMember.getProfileUrl() != null ? updateMember.getProfileUrl() : "";
    }

    public List<String> getBlockTarget() {
        return this.getMemberBlockEntities().stream()
                .map(MemberBlockEntity::getTargetMid)
                .collect(Collectors.toList());
    }
    public void changePhoneNumber(String phoneNumber) throws CustomException {
        this.phone = CryptUtils.encrypt(phoneNumber);
    }
}
