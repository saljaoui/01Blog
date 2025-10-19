package com._blog.backend.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class PostResponse {
    // Post info
    private UUID id;
    private String title;
    private String content;             // Full EditorJS JSON
    
    // Author info
    private UUID authorId;
    private String authorFirstName;
    private String authorLastName;
    
    // Engagement
    private Long likesCount;
    private Long commentsCount;
    private Long savedsCount;
    
    // Time
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String timeAgo;
}