package com.teamside.project.alpha.member.model.dto;

import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_MEMBER_NAME, message = "이름이 올바르지 않습니다.")
    private String name;
    @Pattern(regexp = KeepitConstant.REGEXP_PHONE, message = "핸드폰 번호가 올바르지 않습니다.")
    @NotNull
    private String phone;
    private String profileUrl;
    private String fcmToken;

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
        @NotNull
        private Boolean alarm;
    }

}
