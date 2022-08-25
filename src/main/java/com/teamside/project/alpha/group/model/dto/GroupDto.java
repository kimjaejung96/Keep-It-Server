package com.teamside.project.alpha.group.model.dto;

import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.group.model.enumurate.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_NAME, message = "그룹 이름이 올바르지 않습니다.")
    private String name;
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_DESCRIPTION, message = "그룹설명 벨리데이션이 올바르지 않습니다.")
    private String description;
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_PASSWORD, message = "password가 올바르지 않습니다.")
    private String password;
    @NotNull
    private Boolean usePrivate;
    @NotNull
    private Integer memberQuantity;
    private String profileUrl;
    @NotNull
    private Category category;
}
