package com.teamside.project.alpha.member.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {
    private String mid;
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_MEMBER_NAME, message = "이름이 올바르지 않습니다.")
    private String name;
    @Pattern(regexp = KeepitConstant.REGEXP_PHONE, message = "핸드폰 번호가 올바르지 않습니다.")
    @NotNull
    private String phone;
    private String profileUrl;
    private String fcmToken;

    @Builder(builderMethodName = "MyPageHome_MemberDto")
    public MemberDto(String mid, String name, String phone, String profileUrl) {
        this.mid = mid;
        this.name = name;
        this.phone = phone;
        this.profileUrl = profileUrl;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpDto {
        @Valid
        private Terms terms;
        @Valid
        private MemberDto member;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Terms {
        @NotNull
        private Boolean terms;
        @NotNull
        private Boolean gps;
        @NotNull
        private Boolean collect;
        @NotNull
        private Boolean marketing;
    }

    @Getter
    public static class InviteMemberList {
        String name;
        String mid;

        @QueryProjection
        public InviteMemberList(String name, String mid) {
            this.name = name;
            this.mid = mid;
        }
    }

    @Getter
    public static class UpdateMember {
        @NotNull
        @Pattern(regexp = KeepitConstant.REGEXP_MEMBER_NAME, message = "이름이 올바르지 않습니다.")
        private String name;

        private String profileUrl;
    }
}
