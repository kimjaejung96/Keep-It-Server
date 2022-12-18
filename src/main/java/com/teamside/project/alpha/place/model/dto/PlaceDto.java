package com.teamside.project.alpha.place.model.dto;

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
    public PlaceDto(Long placeId, String placeName, String address, String roadAddress, String phone, BigDecimal x, BigDecimal y) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.address = address;
        this.roadAddress = roadAddress;
        this.phone = phone;
        this.x = x;
        this.y = y;
    }

    @Getter
    @NoArgsConstructor
    public static class PlacePinDto extends PlaceDto{
        private String imageUrl;
        private Long reviewCount;

        public PlacePinDto(Long placeId, String placeName, String address, String roadAddress, String phone, BigDecimal x, BigDecimal y, Long reviewCount, String imageUrl) {
            super(placeId, placeName, address, roadAddress, phone, x, y);
            if (imageUrl != null && imageUrl.split(",").length >= 2) {
                this.imageUrl = imageUrl.split(",")[0];
            } else {
                this.imageUrl = imageUrl;
            }
            this.reviewCount = reviewCount;
        }
    }

}
