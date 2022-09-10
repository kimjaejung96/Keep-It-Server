package com.teamside.project.alpha.group.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class PlaceEntity {
    @Id
    @Column(name = "PLACE_ID", columnDefinition = "bigint")
    private Long placeId;

    @Column(name = "PLACE_NAME", columnDefinition = "varchar(500)")
    private String placeName;

    @Column(name = "ADDRESS", columnDefinition = "varchar(1000)")
    private String address;

    @Column(name = "ROAD_ADDRESS", columnDefinition = "varchar(1000)")
    private String roadAddress;

    @Column(name = "PHONE", columnDefinition = "varchar(13)")
    private String phone;

    @Column(name = "X", nullable = false, columnDefinition = "DECIMAL(16,13)")
    private BigDecimal x;

    @Column(name = "Y", nullable = false, columnDefinition = "DECIMAL(16,14)")
    private BigDecimal y;
}
