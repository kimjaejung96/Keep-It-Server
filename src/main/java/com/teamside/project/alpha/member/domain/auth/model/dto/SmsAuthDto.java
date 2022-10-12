package com.teamside.project.alpha.member.domain.auth.model.dto;

import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsAuthDto {

    @Pattern(regexp = KeepitConstant.REGEXP_PHONE,
            message = "핸드폰 번호가 올바르지 않습니다.")
    private String phone;

    @Pattern(regexp = KeepitConstant.REGEXP_AUTH_NUM,
            message = "인증번호가 올바르지 않습니다.")
    private String authNum;

}
