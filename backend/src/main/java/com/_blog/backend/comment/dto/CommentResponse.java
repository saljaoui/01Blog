package com._blog.backend.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentResponse {
    private UUID id;
    private String content;

    private UUID authorId;
    private String authorFirstName;
    private String authorLastName;
    private boolean owner;

    private UUID postId;
    private LocalDateTime createdAt;
}
