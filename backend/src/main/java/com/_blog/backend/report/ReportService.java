package com._blog.backend.report;

import java.util.List;
import java.util.UUID;


import org.hibernate.usertype.UserType;
import org.springframework.stereotype.Service;

import com._blog.backend.api.ApiResponse;
import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.exception.ResourceNotFoundException;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.report.dto.ReportRequest;
import com._blog.backend.report.dto.ReportResponse;
import com._blog.backend.report.dto.ReportStatusResponse;
import com._blog.backend.report.dto.ReportStatusUpdateRequest;
import com._blog.backend.user.Role;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;
import com._blog.backend.user.UserStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public ApiResponse createReport(ReportRequest request, User reporter) {

        // Validate report type & entity existence
        Report.ReportBuilder reportBuilder = Report.builder()
                .reportId(UUID.randomUUID())
                .reporter(reporter)
                .reason(request.getReason())
                .status(ReportStatus.PENDING)
                .type(request.getType());

        if (request.getType() == ReportType.USER) {

            if (request.getReportedUserId() == null) {
                throw new IllegalArgumentException("Reported user ID is required for USER reports");
            }

            User reportedUser = userRepository.findById(request.getReportedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reported user not found"));

            if (reporter.getId().equals(reportedUser.getId())) {
                throw new IllegalArgumentException("Cannot report yourself");
            }
            if (reportedUser.getStatus() == UserStatus.BANNED) {
                throw new IllegalArgumentException("Cannot report a banned user");
            }
            if (reportedUser.getRole().equals(Role.ADMIN)) {
                throw new IllegalArgumentException("Cannot report an admin user");
            }

            // ✅ Prevent duplicate report
            boolean alreadyReported = reportRepository.existsByReporterAndReportedUser(reporter, reportedUser);
            if (alreadyReported) {
                throw new IllegalArgumentException("You have already reported this user");
            }

            reportBuilder.reportedUser(reportedUser);

        } else if (request.getType() == ReportType.POST) {

            if (request.getReportedPostId() == null) {
                throw new IllegalArgumentException("Reported post ID is required for POST reports");
            }

            Post reportedPost = postRepository.findById(request.getReportedPostId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reported post not found"));

            if (reporter.getId().equals(reportedPost.getUser().getId())) {
                throw new IllegalArgumentException("Cannot report your own post");
            }

            // ✅ Prevent duplicate report
            boolean alreadyReported = reportRepository.existsByReporterAndReportedPost(reporter, reportedPost);
            if (alreadyReported) {
                throw new IllegalArgumentException("You have already reported this post");
            }

            reportBuilder.reportedPost(reportedPost);

        } else {
            throw new IllegalArgumentException("Invalid report type");
        }

        Report report = reportBuilder.build();
        reportRepository.save(report);

        return ApiResponse.builder()
                .success(true)
                .message("Report created successfully")
                .build();
    }

    public ReportResponse banUserFromReport(UUID userId, UUID reportId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);

        return mapToResponse(report);
    }

    public ReportResponse dismissReport(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }

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
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return mapToResponse(report);
    }

    public ReportResponse updateReportStatus(UUID reportId, ReportStatusUpdateRequest request) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(request.getStatus());
        reportRepository.save(report);
        return mapToResponse(report);
    }

    public void deleteReport(UUID reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }

        if (!reportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Report not found");
        }
        reportRepository.deleteById(reportId);
    }

    private ReportResponse mapToResponse(Report report) {
        UUID reportedUserId = null;
        String reportedUserUsername = null;

        if (report.getReportedUser() != null) {
            reportedUserId = report.getReportedUser().getId();
            reportedUserUsername = report.getReportedUser().getUsername();
        }

        else if (report.getReportedPost() != null && report.getReportedPost().getUser() != null) {
            reportedUserId = report.getReportedPost().getUser().getId();
            reportedUserUsername = report.getReportedPost().getUser().getUsername();
        }

        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reportedUserId(reportedUserId)
                .reportedUserUsername(reportedUserUsername)
                .reportedPostId(report.getReportedPost() != null ? report.getReportedPost().getId() : null)
                .reason(report.getReason())
                .timestamp(report.getTimestamp())
                .status(report.getStatus())
                .type(report.getType())
                .build();
    }
}
