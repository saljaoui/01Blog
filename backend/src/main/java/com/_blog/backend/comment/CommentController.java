package com._blog.backend.comment;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.comment.dto.CommentLikeRequest;
import com._blog.backend.comment.dto.CommentLikeResponse;
import com._blog.backend.comment.dto.CommentRequest;
import com._blog.backend.comment.dto.CommentResponse;
import com._blog.backend.user.User;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID postId,
            @RequestBody CommentRequest request) {

        User user = SecurityUtils.getCurrentUser();

        CommentResponse response = commentService.createComment(
                postId,
                request,
                user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        User user = SecurityUtils.getCurrentUser();
        commentService.deleteComment(commentId, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/like")
    public ResponseEntity<CommentLikeResponse> toggleCommentLike(@RequestBody CommentLikeRequest request) {
        return ResponseEntity.ok(commentService.toggleCommentLike(request.getCommentId()));
    }

    @GetMapping("/like")
    public ResponseEntity<CommentLikeResponse> getCommentLikeStatus(@RequestParam UUID commentId) {
        return ResponseEntity.ok(commentService.getCommentLikeStatus(commentId));
    }
}
