package com.teamside.project.alpha.group.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDto {
    @NotNull
    private long groupId;
    @NotNull
    private long placeId;
    @Size(min = 15, max = 2000)
    private String content;
    private List<String> images;

    @Getter
    public static class UpdateReviewDto extends ReviewDto{
        private long reviewId;
    }
}
