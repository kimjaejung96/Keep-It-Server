package com.teamside.project.alpha.group.model.dto;

import com.teamside.project.alpha.group.model.enumurate.Category;
import lombok.Getter;

@Getter
public class GroupDto {
    private String name;
    private String description;
    private String password;
    private Boolean usePrivate;
    private Integer memberQuantity;
    private String profileUrl;
    private Category category;
}
