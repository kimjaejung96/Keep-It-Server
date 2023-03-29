package com.teamside.project.alpha.place.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.place.model.dto.PlaceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "PLACE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class PlaceEntity extends TimeEntity {
    @Id
    @Column(name = "PLACE_ID", columnDefinition = "bigint")
    private Long placeId;

    @Column(name = "PLACE_NAME", columnDefinition = "varchar(500)")
    private String placeName;

    @Column(name = "ADDRESS", columnDefinition = "varchar(1000)")
    private String address;

    @Column(name = "ROAD_ADDRESS", columnDefinition = "varchar(1000)")
    private String roadAddress;

    @Column(name = "PHONE", columnDefinition = "varchar(15)")
    private String phone;

    @Column(name = "CATEGORY_GROUP_CODE", columnDefinition = "varchar(3)")
    private String categoryGroupCode;

    @Column(name = "CATEGORY_NAME", columnDefinition = "varchar(100)")
    private String categoryName;

    @Column(name = "X", nullable = false, columnDefinition = "DECIMAL(16,13)")
    private BigDecimal x;

    @Column(name = "Y", nullable = false, columnDefinition = "DECIMAL(16,14)")
    private BigDecimal y;

    public PlaceEntity(Long placeId) {
        this.placeId = placeId;
    }

    public PlaceEntity(PlaceDto placeDto) {
        this.placeId = placeDto.getPlaceId();
        this.placeName = placeDto.getPlaceName();
        this.address = placeDto.getAddress();
        this.roadAddress = placeDto.getRoadAddress();
        this.phone = placeDto.getPhone();
        this.categoryGroupCode = placeDto.getCategoryGroupCode();
        this.categoryName = placeDto.getCategoryName();
        this.x = placeDto.getX();
        this.y = placeDto.getY();
    }

    public void placeCategoryCheck(PlaceDto place) {
        if (Strings.isBlank(this.categoryGroupCode) || Strings.isBlank(this.categoryName)) {
            this.categoryGroupCode = place.getCategoryGroupCode() != null
                    ? place.getCategoryGroupCode() : "";
            this.categoryName = place.getCategoryName() != null
                    ? place.getCategoryName() : "";
        }
    }
}
