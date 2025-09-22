package com._blog.backend.post.dto;



import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    private String title;
    private List<PostBlockRequest> postBlocks;
}
