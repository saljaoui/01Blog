package com._blog.backend.like.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeRequest {
    private UUID postId;
}
