package com._blog.backend.post.dto;

import lombok.Data;

@Data
public class PostBlockRequest {
    private Integer position;
    private String type;
    private String content;
}
