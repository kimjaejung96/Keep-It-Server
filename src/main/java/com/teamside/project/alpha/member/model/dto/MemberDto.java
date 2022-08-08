package com.teamside.project.alpha.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String mid;
    @Pattern(regexp = "(?=.*[-_A-Za-z0-9])(?=.*[^-_]).{4,20}",
            message = "이름이 올바르지 않습니다.")
    private String name;
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "핸드폰 번호가 올바르지 않습니다.")
    private String phone;
    private String profileUrl;
    private String pinProfileUrl;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpDto {
        private Terms terms;
        private MemberDto member;
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Terms {
            private boolean terms;
            private boolean collect;
            private boolean marketing;
            private boolean alarm;
        }
    }
}
