package com._blog.backend.save.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavedRequest {
    private UUID postId;
}
