package com._blog.backend.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.comment.dto.CommentLikeResponse;
import com._blog.backend.comment.dto.CommentRequest;
import com._blog.backend.comment.dto.CommentResponse;
import com._blog.backend.exception.ResourceNotFoundException;
import com._blog.backend.exception.UnauthorizedException;
import com._blog.backend.notification.NotificationService;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse createComment(UUID postId, CommentRequest commentRequest, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .content(commentRequest.getContent())
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        notificationService.createCommentNotification(postId, user.getUsername());

        return mapToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        return comments.stream()
                .map(comment -> CommentResponse
                        .builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .authorId(comment.getUser().getId())
                        .authorFirstName(comment.getUser().getFirstName())
                        .authorLastName(comment.getUser().getLastName())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public long getCommentCount(Post post) {
        return commentRepository.countByPost(post);
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException(
                    "You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentLikeResponse toggleCommentLike(UUID commentId, User user) {
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

    public CommentLikeResponse getCommentLikeStatus(UUID commentId, User user) {
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

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getUser().getId())
                .authorFirstName(comment.getUser().getFirstName())
                .authorLastName(comment.getUser().getLastName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
