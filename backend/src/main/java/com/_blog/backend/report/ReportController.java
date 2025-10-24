package com._blog.backend.report;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.report.dto.ReportRequest;
import com._blog.backend.report.dto.ReportResponse;
import com._blog.backend.report.dto.ReportStatusUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable UUID id,
            @RequestBody ReportStatusUpdateRequest request) {
        return ResponseEntity.ok(reportService.updateReportStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
