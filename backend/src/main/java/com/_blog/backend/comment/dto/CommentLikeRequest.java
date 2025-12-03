package com._blog.backend.comment.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentLikeRequest {
    @NotNull(message = "Comment ID cannot be null")
    private UUID commentId;
}
