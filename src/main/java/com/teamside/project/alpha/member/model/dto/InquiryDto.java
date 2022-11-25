package com.teamside.project.alpha.member.model.dto;

import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.member.model.entity.InquiryEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryDto {

    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_EMAIL,
            message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_MEMBER_NAME,
            message = "이름이 올바르지 않습니다.")
    private String name;

    @NotNull
    @Size(max = 100, message = "최대 100자 입력가능합니다.")
    private String place;

    @NotNull
    @Size(max = 100, message = "최대 100자 입력가능합니다.")
    private String world;

    @Size(max = 200, message = "최대 200자 입력가능합니다.")
    private String etc;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyInquiry {
        @NotNull
        @Pattern(regexp = KeepitConstant.REGEXP_EMAIL,
                message = "올바른 이메일 주소를 입력해주세요.")
        private String email;

        @NotNull
        @Size(min = 2, max = 1000, message = "최소 2자에서 최대 1000자 입력가능합니다.")
        private String detail;

        public InquiryEntity toEntity() {
            return new InquiryEntity(
                    this.getEmail(),
                    null,
                    null,
                    null,
                    null,
                    this.detail
            );
        }
    }
}
