package com._blog.backend.post.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String title;
    private Long time;         // Timestamp from EditorJS
    private String version;      // EditorJS version
    private List<BlockRequest> blocks;  // Content blocks
}