package com.teamside.project.alpha.report.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.CreateDtEntity;
import com.teamside.project.alpha.report.model.enumerate.ReportType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "REPORT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Builder
@AllArgsConstructor
public class ReportEntity extends CreateDtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_ID", columnDefinition = "bigint")
    private Long reportId;

    @Column(name = "TYPE", columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Column(name = "TYPE_ID", columnDefinition = "varchar(36)")
    private String typeId;

    @Column(name = "REPORTER_MID", columnDefinition = "varchar(36)")
    private String reporterMid;

}
