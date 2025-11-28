package com._blog.backend.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._blog.backend.exception.ResourceNotFoundException;
import com._blog.backend.follow.FollowRepository;
import com._blog.backend.notification.dto.NotificationResponse;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().equals(user)) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void toggleRead(Long notificationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().equals(user)) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }

        if (notification.isRead()) {
            notification.setRead(false);
        } else {
            notification.setRead(true);
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void createLikeNotification(UUID postId, String actorUsername) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User actor = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Don't create notification if user likes their own post
        if (post.getUser().equals(actor)) {
            return;
        }

        Notification notification = Notification.builder()
                .recipient(post.getUser())
                .actor(actor)
                .type(NotificationType.LIKE_POST)
                .postId(postId)
                .message(actor.getFirstName() + " " + actor.getLastName() + " liked your post \"" + post.getTitle()
                        + "\"")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        if (notification == null) {
            throw new IllegalArgumentException("Notification entity cannot be null");
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void createCommentNotification(UUID postId, String actorUsername) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User actor = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Don't create notification if user comments on their own post
        if (post.getUser().equals(actor)) {
            return;
        }

        Notification notification = Notification.builder()
                .recipient(post.getUser())
                .actor(actor)
                .type(NotificationType.COMMENT_POST)
                .postId(postId)
                .message(actor.getFirstName() + " " + actor.getLastName() + " commented on your post \""
                        + post.getTitle() + "\"")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        if (notification == null) {
            throw new IllegalArgumentException("Notification entity cannot be null");
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void createFollowNotification(String followingUsername, String actorUsername) {
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User actor = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .recipient(following)
                .actor(actor)
                .type(NotificationType.FOLLOW_USER)
                .message(actor.getFirstName() + " " + actor.getLastName() + " started following you")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        if (notification == null) {
            throw new IllegalArgumentException("Notification entity cannot be null");
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void createNewPostNotification(Post post) {
        User author = post.getUser();

        // Get all followers of the author
        List<User> followers = followRepository.findAllByFollowing(author)
                .stream()
                .map(follow -> follow.getFollower())
                .toList();

        for (User follower : followers) {
            Notification notification = Notification.builder()
                    .recipient(follower)
                    .actor(author)
                    .type(NotificationType.NEW_POST_FROM_FOLLOWING)
                    .postId(post.getId())
                    .message(author.getFirstName() + " " + author.getLastName() + " published a new post: \""
                            + post.getTitle() + "\"")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();

            if (notification == null) {
                throw new IllegalArgumentException("Notification entity cannot be null");
            }

            notificationRepository.save(notification);
        }
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .actorId(notification.getActor().getId())
                .actorFirstName(notification.getActor().getFirstName())
                .actorLastName(notification.getActor().getLastName())
                .actorAvatar(notification.getActor().getAvatarUrl())
                .type(notification.getType())
                .postId(notification.getPostId())
                .commentId(notification.getCommentId())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }
}
