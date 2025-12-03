package com._blog.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    @NotBlank(message = "Comment content must not be empty")
    @Size(min = 1, max = 2000, message = "Comment must be between 5 and 2000 characters")
    private String content;
}
