package com._blog.backend.report;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Long countByStatus(ReportStatus status);
    Optional<Report> findById(UUID id);
}
