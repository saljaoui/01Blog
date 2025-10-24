package com._blog.backend.comment.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentLikeRequest {
    private UUID commentId;
}
