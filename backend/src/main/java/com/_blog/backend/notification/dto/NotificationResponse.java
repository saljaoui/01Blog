package com._blog.backend.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com._blog.backend.notification.NotificationType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotificationResponse {
    private Long id;
    private UUID actorId;
    private String actorFirstName;
    private String actorLastName;
    private NotificationType type;
    private UUID postId;
    private UUID commentId;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
}
