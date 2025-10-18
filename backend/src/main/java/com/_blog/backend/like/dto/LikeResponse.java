package com._blog.backend.like.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeResponse {
    private boolean liked;
    private long likesCount;
}
