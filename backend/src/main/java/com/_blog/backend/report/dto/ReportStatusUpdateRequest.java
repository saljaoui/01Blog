package com._blog.backend.report.dto;

import com._blog.backend.report.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatusUpdateRequest {
    private ReportStatus status;
}
