package com._blog.backend.report;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog.backend.post.Post;
import com._blog.backend.user.User;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Long countByStatus(ReportStatus status);
    boolean existsByReporterAndReportedPost(User reporter, Post reportedPost);
    boolean existsByReporterAndReportedUser(User reporter, User reportedUser);
    Long countByReportedPost_Id(UUID id);
}
