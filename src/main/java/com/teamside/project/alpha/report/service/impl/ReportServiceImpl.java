package com.teamside.project.alpha.report.service.impl;

import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.report.model.dto.ReportDto;
import com.teamside.project.alpha.report.model.entity.ReportEntity;
import com.teamside.project.alpha.report.repository.ReportRepository;
import com.teamside.project.alpha.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    @Override
    public void report(ReportDto reportDto) {
        ReportEntity reportEntity = ReportEntity.builder()
                .type(reportDto.getReportType())
                .typeId(reportDto.getTypeId())
                .reporterMid(CryptUtils.getMid())
                .build();
        reportRepository.save(reportEntity);
    }
}
