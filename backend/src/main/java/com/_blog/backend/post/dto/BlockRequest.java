package com._blog.backend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequest {
    private String type; // "header", "paragraph", "image", etc.
    private Object data; // Block data (varies by type)
}
