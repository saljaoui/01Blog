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
    private long likesCount;            // Full EditorJS JSON
    private boolean liked;
    private long savesCount;            // Full EditorJS JSON
    private boolean saved;
    private long commentsCount;
    
    // Author info
    private UUID authorId;
    private String authorUsername;
    private String authorFirstName;
    private String authorLastName;
    private boolean owner;
    
    // Time
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}