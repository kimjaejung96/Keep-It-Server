package com.teamside.project.alpha.group.model.enumurate;

import lombok.Getter;

@Getter
public enum MyGroupType {
    NULL("NULL"),
    ALL("ALL"),
    FAVORITE("FAVORITE");

    private final String type;

    MyGroupType(String type) {
        this.type = type;
    }
}
