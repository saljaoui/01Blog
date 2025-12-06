package com._blog.backend.report.dto;

import java.util.UUID;

import com._blog.backend.report.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private UUID reportedUserId;
    private UUID reportedPostId;
    @NotNull(message = "Report type is required")
    private ReportType type;
    @NotBlank(message = "Reason is required")
    private String reason;
}
