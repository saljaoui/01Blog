package com._blog.backend.report;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.exception.ResourceNotFoundException;
import com._blog.backend.post.PostRepository;
import com._blog.backend.report.dto.ReportRequest;
import com._blog.backend.report.dto.ReportResponse;
import com._blog.backend.report.dto.ReportStatusResponse;
import com._blog.backend.report.dto.ReportStatusUpdateRequest;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ReportResponse createReport(ReportRequest request) {
        User reporter = SecurityUtils.getCurrentUser();
        User reportedUser = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Reported user not found"));

        // Prevent self-reporting
        if (reporter.getId().equals(reportedUser.getId())) {
            throw new IllegalArgumentException("Cannot report yourself");
        }

        Report report = Report.builder()
                .reportId(UUID.randomUUID())
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(request.getReason())
                .status(ReportStatus.PENDING)
                .type(request.getType())
                .build();

        reportRepository.save(report);
        return mapToResponse(report);
    }

    public ReportResponse dismissReport(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);

        return mapToResponse(report);
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ReportStatusResponse getReportStatus() {
        Long totalUsers = userRepository.count();
        Long totalPosts = postRepository.count();
        Long totalPendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        return ReportStatusResponse.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .totalPendingReports(totalPendingReports)
                .build();
    }

    public ReportResponse getReportById(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return mapToResponse(report);
    }

    public ReportResponse updateReportStatus(UUID reportId, ReportStatusUpdateRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(request.getStatus());
        reportRepository.save(report);
        return mapToResponse(report);
    }

    public void deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Report not found");
        }
        reportRepository.deleteById(reportId);
    }

    private ReportResponse mapToResponse(Report report) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reportedUserId(report.getReportedUser().getId())
                .reportedUserUsername(report.getReportedUser().getUsername())
                .reason(report.getReason())
                .timestamp(report.getTimestamp())
                .status(report.getStatus())
                .type(report.getType())
                .build();
    }
}
