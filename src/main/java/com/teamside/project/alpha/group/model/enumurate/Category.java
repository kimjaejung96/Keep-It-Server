package com.teamside.project.alpha.group.model.enumurate;

import lombok.Getter;

@Getter
public enum Category {
    MEETING("MEETING"),
    RESTAURANT("RESTAURANT"),
    HOBBY("HOBBY"),
    REGION("REGION");

    private final String value;

    Category(String value) {
        this.value = value;
    }
}

/**
 * Meeting Restaurant Local Hobby Leisure
 */
