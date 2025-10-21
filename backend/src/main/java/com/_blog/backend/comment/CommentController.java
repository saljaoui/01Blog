package com._blog.backend.comment;

import java.security.Principal;
import java.security.Security;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.comment.dto.CommentRequest;
import com._blog.backend.comment.dto.CommentResponse;
import com._blog.backend.config.SecurityConfig;
import com._blog.backend.user.User;

import jakarta.validation.Valid;

@RestController  // ✅ Changed from @RestControllerAdvice
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Create a comment on a specific post
    @PostMapping("/post/{postId}")  // ✅ Added postId in path
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequest request) {

                User user = SecurityUtils.getCurrentUser();
        
        CommentResponse response = commentService.createComment(
            postId, 
            request, 
            user.getUsername()
            );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all comments for a specific post
    @GetMapping("/post/{postId}")  // ✅ Changed path to be more RESTful
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable UUID postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);  // ✅ Return actual data
    }
}