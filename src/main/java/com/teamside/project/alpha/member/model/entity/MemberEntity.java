package com.teamside.project.alpha.member.model.entity;


import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import com.teamside.project.alpha.member.domain.auth.model.entity.RefreshTokenEntity;
import com.teamside.project.alpha.member.model.dto.MemberDto;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Column(name = "PHONE", columnDefinition = "char(24)")
    private String phone;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Column(name="FCM_TOKEN", columnDefinition = "varchar(170)")
    private String fcmToken;

    @Column(name="TYPE", columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private SignUpType type;

    @OneToOne(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    private TermsEntity termsEntity;
    @OneToOne(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    private RefreshTokenEntity refreshTokenEntity;
    @OneToMany(mappedBy = "member",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MemberFollowEntity> memberFollowEntities;
    @OneToMany(mappedBy = "targetMember",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MemberFollowEntity> memberFollowTargetEntities;
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

    public void follow(String mid, String targetMid) {
        // 이미 팔로중이면 취소
        Optional<MemberFollowEntity> followEntity = this.getMemberFollowEntities().stream()
                .filter(follow -> follow.getMid().equals(mid) && follow.getTargetMid().equals(targetMid))
                .findFirst();

        if (followEntity.isPresent()) {
            this.memberFollowEntities.remove(followEntity.get());
        } else {
            this.memberFollowEntities.add(new MemberFollowEntity(mid, targetMid));
        }
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
        this.refreshTokenEntity = null;
        this.memberBlockEntities = null;
        this.memberFollowEntities = null;
        this.memberFollowTargetEntities = null;
        // status -> true
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
