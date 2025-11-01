package com._blog.backend.post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.comment.CommentRepository;
import com._blog.backend.like.LikeRepository;
import com._blog.backend.notification.NotificationService;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;
import com._blog.backend.report.ReportRepository;
import com._blog.backend.save.SavedRepository;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ReportRepository reportRepository;
    private final SavedRepository savedRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public PostResponse create(PostRequest postRequest) {
        User user = SecurityUtils.getCurrentUser();
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .user(user)
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();

        if (post != null) {
            postRepository.save(post);
        }

        // Create notifications for followers
        notificationService.createNewPostNotification(post);

        return new PostResponse();
    }

    public PostResponse updatePost(UUID postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post not found"));
        if (post != null) {
            post.setContent(postRequest.getContent());
            post.setTitle(postRequest.getTitle());
            postRepository.save(post);
        }

        return new PostResponse();
    }

    public List<PostResponse> getAllPosts(int page, int size) {
        User user = SecurityUtils.getCurrentUser();
        UUID currentUserId = user.getId();

        return postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))).stream()
                .map(post -> PostResponse
                        .builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likesCount(likeRepository.countByPost(post))
                        .liked(likeRepository.existsByPostAndUser(post, user))
                        .savesCount(savedRepository.countByPost(post))
                        .saved(savedRepository.existsByPostAndUser(post, user))
                        .commentsCount(commentRepository.countByPost(post))
                        .reportsCount(reportRepository.countByReportedPost_Id(post.getId()))
                        .authorId(post.getUser().getId())
                        .authorUsername(post.getUser().getUsername())
                        .authorFirstName(post.getUser().getFirstName())
                        .authorLastName(post.getUser().getLastName())
                        .authorAvatar(post.getUser().getAvatarUrl())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .owner(post.getUser().getId().equals(currentUserId))
                        .build())
                .toList();
    }

    public List<PostResponse> getAllPosts() {
        return getAllPosts(0, Integer.MAX_VALUE);
    }

    public PostResponse getPostById(UUID postId) {
        return postRepository.findById(postId)
                .map(post -> PostResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likesCount(likeRepository.countByPost(post))
                        .liked(likeRepository.existsByPostAndUser(post, SecurityUtils.getCurrentUser()))
                        .savesCount(savedRepository.countByPost(post))
                        .saved(savedRepository.existsByPostAndUser(post, SecurityUtils.getCurrentUser()))
                        .commentsCount(commentRepository.countByPost(post))
                        .authorId(post.getUser().getId())
                        .authorUsername(post.getUser().getUsername())
                        .authorFirstName(post.getUser().getFirstName())
                        .authorLastName(post.getUser().getLastName())
                        .authorAvatar(post.getUser().getAvatarUrl())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .owner(post.getUser().getId().equals(SecurityUtils.getCurrentUser().getId()))
                        .build())
                .orElse(null);
    }

    public List<PostResponse> getPostsByUser(UUID userId) {
        User currentUser = SecurityUtils.getCurrentUser();
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUser(targetUser).stream()
                .map(post -> PostResponse
                        .builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likesCount(likeRepository.countByPost(post))
                        .liked(likeRepository.existsByPostAndUser(post, currentUser))
                        .savesCount(savedRepository.countByPost(post))
                        .saved(savedRepository.existsByPostAndUser(post, currentUser))
                        .commentsCount(commentRepository.countByPost(post))
                        .authorId(post.getUser().getId())
                        .authorUsername(post.getUser().getUsername())
                        .authorFirstName(post.getUser().getFirstName())
                        .authorLastName(post.getUser().getLastName())
                        .authorAvatar(post.getUser().getAvatarUrl())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .owner(post.getUser().getId().equals(currentUser.getId()))
                        .build())
                .toList();
    }

    public static PostResponse toPostResponse(Post post, User user) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .likesCount(0L) // Will be set by caller if needed
                .liked(false) // Will be set by caller if needed
                .savesCount(0L) // Will be set by caller if needed
                .saved(false) // Will be set by caller if needed
                .commentsCount(0L) // Will be set by caller if needed
                .authorId(post.getUser().getId())
                .authorUsername(post.getUser().getUsername())
                .authorFirstName(post.getUser().getFirstName())
                .authorLastName(post.getUser().getLastName())
                .authorAvatar(post.getUser().getAvatarUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .owner(post.getUser().getId().equals(user.getId()))
                .build();
    }
}
