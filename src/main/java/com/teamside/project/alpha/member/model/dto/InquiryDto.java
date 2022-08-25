package com.teamside.project.alpha.member.model.dto;

import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

}
