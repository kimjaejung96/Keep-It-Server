package com.teamside.project.alpha.report.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.report.model.dto.ReportDto;
import com.teamside.project.alpha.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> report(@RequestBody ReportDto reportDto) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reportService.report(reportDto);
        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }
}
