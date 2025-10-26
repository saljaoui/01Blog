package com._blog.backend.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportStatusResponse {
    private Long totalUsers;
    private Long totalPendingReports;
    private Long totalPosts;
}
