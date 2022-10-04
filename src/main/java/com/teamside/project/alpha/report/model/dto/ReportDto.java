package com.teamside.project.alpha.report.model.dto;

import com.teamside.project.alpha.report.model.enumerate.ReportType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportDto {
    private ReportType reportType;
    private Long typeId;
}
