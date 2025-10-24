package com._blog.backend.report.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com._blog.backend.report.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {
    private UUID reportId;
    private UUID reporterId;
    private String reporterUsername;
    private UUID reportedUserId;
    private String reportedUserUsername;
    private String reason;
    private LocalDateTime timestamp;
    private ReportStatus status;
}
