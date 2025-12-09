package com._blog.backend.post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com._blog.backend.comment.CommentRepository;
import com._blog.backend.follow.Follow;
import com._blog.backend.follow.FollowRepository;
import com._blog.backend.like.LikeRepository;
import com._blog.backend.notification.NotificationService;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;
import com._blog.backend.report.ReportRepository;
import com._blog.backend.save.SavedRepository;
import com._blog.backend.user.Role;
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
    private final FollowRepository followRepository;

    public PostResponse create(PostRequest postRequest, User user) {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .user(user)
                .title(postRequest.getTitle())
                .hidden(false)
                .content(postRequest.getContent().replaceAll("&nbsp;", "").trim())
                .build();

        if (post != null) {
            postRepository.save(post);
        }

        // Create notifications for followers
        notificationService.createNewPostNotification(post);

        return new PostResponse();
    }

    public PostResponse updatePost(UUID postId, PostRequest postRequest, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to edit this post");
        }

        post.setContent(postRequest.getContent().replaceAll("&nbsp;", "").trim());
        post.setTitle(postRequest.getTitle());
        postRepository.save(post);

        return new PostResponse();
    }

    public List<PostResponse> getAllPosts(int page, int size, User user) {
        UUID currentUserId = user.getId();

        return postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))).stream()
                .filter(post -> !post.isHidden() || user.getRole().equals(Role.ADMIN))
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
                        .hidden(post.isHidden())
                        .build())
                .toList();
    }

    public PostResponse getPostById(UUID postId, User user) {
        return postRepository.findById(postId)
                .map(post -> PostResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likesCount(likeRepository.countByPost(post))
                        .liked(likeRepository.existsByPostAndUser(post, user))
                        .savesCount(savedRepository.countByPost(post))
                        .saved(savedRepository.existsByPostAndUser(post, user))
                        .commentsCount(commentRepository.countByPost(post))
                        .authorId(post.getUser().getId())
                        .authorUsername(post.getUser().getUsername())
                        .authorFirstName(post.getUser().getFirstName())
                        .authorLastName(post.getUser().getLastName())
                        .authorAvatar(post.getUser().getAvatarUrl())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .owner(post.getUser().getId().equals(user.getId()))
                        .build())
                .orElse(null);
    }

    public List<PostResponse> getPostsByUser(UUID userId, User user) {
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUser(targetUser).stream()
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
                        .authorId(post.getUser().getId())
                        .authorUsername(post.getUser().getUsername())
                        .authorFirstName(post.getUser().getFirstName())
                        .authorLastName(post.getUser().getLastName())
                        .authorAvatar(post.getUser().getAvatarUrl())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .owner(post.getUser().getId().equals(user.getId()))
                        .build())
                .toList();
    }

    public List<PostResponse> getFollowedPosts(int page, int size, User user) {

        // Get list of users that the current user is following
        List<User> followedUsers = followRepository.findAllByFollower(user).stream()
                .map(Follow::getFollowing)
                .toList();

        if (followedUsers.isEmpty()) {
            return List.of(); // Return empty list if no followed users
        }

        return postRepository
                .findByUserIn(followedUsers, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
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
                        .owner(post.getUser().getId().equals(user.getId()))
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

    public Post getPostEntityById(UUID postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }

        return postRepository.findById(postId).orElse(null);
    }

    public void deletePost(UUID postId) {
        System.out.println("🛠️ Starting deletion of post with ID: " + postId);

        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("❌ Post not found"));
        System.out.println("✅ Post found: " + post.getId());

        // Now delete the post
        System.out.println("🔥 Deleting the post itself: " + postId);
        postRepository.delete(post);
        System.out.println("✅ Post deletion complete: " + postId);
    }

    public void togglePostVisibility(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setHidden(!post.isHidden());
        postRepository.save(post);
    }
}
