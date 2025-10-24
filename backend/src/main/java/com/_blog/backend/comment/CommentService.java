package com._blog.backend.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.comment.dto.CommentLikeRequest;
import com._blog.backend.comment.dto.CommentLikeResponse;
import com._blog.backend.comment.dto.CommentRequest;
import com._blog.backend.comment.dto.CommentResponse;
import com._blog.backend.exception.ResourceNotFoundException;
import com._blog.backend.exception.UnauthorizedException;
import com._blog.backend.notification.NotificationService;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

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
        .id(UUID.randomUUID())
        .content(commentRequest.getContent())
        .post(post)
        .user(user)
        .createdAt(LocalDateTime.now())
        .build()
        ;

        Comment savedComment = commentRepository.save(comment);

        // Create notification for the post author
        notificationService.createCommentNotification(postId, username);

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

        // Delete all associated likes first
        commentLikeRepository.deleteAllByComment(comment);

        // Then delete the comment
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
    
    @Transactional
    public CommentLikeResponse toggleCommentLike(UUID commentId) {
        User user = SecurityUtils.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean alreadyLiked = commentLikeRepository.existsByCommentAndUser(comment, user);

        if (alreadyLiked) {
            commentLikeRepository.deleteByCommentAndUser(comment, user);
        } else {
            CommentLike like = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(like);
        }

        long likesCount = commentLikeRepository.countByComment(comment);

        return CommentLikeResponse.builder()
                .liked(!alreadyLiked)
                .likesCount(likesCount)
                .build();
    }

    public CommentLikeResponse getCommentLikeStatus(UUID commentId) {
        User user = SecurityUtils.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isLiked = commentLikeRepository.existsByCommentAndUser(comment, user);
        long likesCount = commentLikeRepository.countByComment(comment);

        return CommentLikeResponse.builder()
                .liked(isLiked)
                .likesCount(likesCount)
                .build();
    }

    public long getCommentLikeCount(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return commentLikeRepository.countByComment(comment);
    }

    /**
     * Helper method to map Comment entity to CommentResponse DTO
     */
}
