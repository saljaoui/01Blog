package com._blog.backend.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._blog.backend.comment.dto.CommentRequest;
import com._blog.backend.comment.dto.CommentResponse;
import com._blog.backend.exception.ResourceNotFoundException;
import com._blog.backend.exception.UnauthorizedException;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(UUID postId, CommentRequest commentRequest, String username) {
        // Find the post
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Find the user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Create comment
        Comment comment = Comment.builder()
        .content(commentRequest.getContent())
        .post(post)
        .user(user)
        .createdAt(LocalDateTime.now())
        .build()
        ;

        Comment savedComment = commentRepository.save(comment);
        
        return CommentResponse
            .builder()
            .id(savedComment.getId())
            .content(savedComment.getContent())
            .authorId(savedComment.getUser().getId())
            .authorFirstName(savedComment.getUser().getFirstName())
            .authorLastName(savedComment.getUser().getLastName())
            .postId(savedComment.getPost().getId())
            .createdAt(savedComment.getCreatedAt())
            .build()
            ;
    }
    
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(UUID postId) {
        // Verify post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        
        return comments.stream()  
            .map(post -> CommentResponse
            .builder()
            .id(post.getId())
            .content(post.getContent())
            .authorId(post.getUser().getId())
            .authorFirstName(post.getUser().getFirstName())
            .authorLastName(post.getUser().getLastName())
            .createdAt(post.getCreatedAt())
            .build()
            )
            .toList()
            ;
    }
    

    @Transactional(readOnly = true)
    public long getCommentCount(Post post) {
        return commentRepository.countByPost(post);
    }
    
    /**
     * Delete a comment (by owner or admin)
     */
    @Transactional
    public void deleteComment(UUID commentId, String username) {
        // Find comment
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        // Check permission: owner or admin
        if (!comment.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to delete this comment");
        }
        
        commentRepository.delete(comment);
    }
    
    /**
     * Delete all comments for a post (called when deleting a post)
     */
    @Transactional
    public void deleteCommentsByPostId(UUID postId) {
        commentRepository.deleteByPostId(postId);
    }
    
    /**
     * Delete all comments by a user (called when deleting/banning a user)
     */
    @Transactional
    public void deleteCommentsByUserId(UUID userId) {
        commentRepository.deleteByUserId(userId);
    }
    
    /**
     * Helper method to map Comment entity to CommentResponse DTO
     */
}