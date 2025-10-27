package com._blog.backend.report.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private UUID reportedUserId;
    private UUID reportedPostId;
    private String reason;
}
