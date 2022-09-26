package com.teamside.project.alpha.group.model.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ReviewDto {
    private ReviewDto() {
    }

    @Getter
    public static class CreateReviewDto {
        @NotNull
        private long groupId;
        @NotNull
        private long placeId;
        @Size(min = 15, max = 2000)
        private String content;
        private List<String> images;
    }
}
