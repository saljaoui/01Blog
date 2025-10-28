package com._blog.backend.report.dto;

import java.util.UUID;

import com._blog.backend.report.ReportType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private UUID reportedUserId;
    private UUID reportedPostId;
    private ReportType type;
    private String reason;
}
