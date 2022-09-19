package com.teamside.project.alpha.group.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceDto {
    private Long placeId;
    private String placeName;
    private String address;
    private String roadAddress;
    private String phone;
    private BigDecimal x;
    private BigDecimal y;

}
