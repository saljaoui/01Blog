package com._blog.backend.like.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeRequest {
    @NotNull(message = "Post ID is required")
    private UUID postId;
}
