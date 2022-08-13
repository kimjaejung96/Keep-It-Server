package com.teamside.project.alpha.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    @NotNull
    @Pattern(regexp = "(?=.*[-_A-Za-z0-9])(?=.*[^-_]).{4,20}",
            message = "이름이 올바르지 않습니다.")
    private String name;
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "핸드폰 번호가 올바르지 않습니다.")
    @NotNull
    private String phone;
    private String profileUrl;
    private String pinProfileUrl;
    private String fcmToken;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpDto {
        @Valid
        private Terms terms;
        @Valid
        private MemberDto member;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
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
