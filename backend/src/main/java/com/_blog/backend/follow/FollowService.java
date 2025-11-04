package com._blog.backend.follow;

import com._blog.backend.notification.NotificationService;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public void follow(User follower, UUID followingId) {
        User following = getUser(followingId);
        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow follow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            if (follow == null) {
                throw new IllegalArgumentException("Follow relationship cannot be null");
            }
            followRepository.save(follow);

            notificationService.createFollowNotification(following.getUsername(), follower.getUsername());
        }
    }

    public void unfollow(User follower, UUID followingId) {
        User following = getUser(followingId);
        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

    public boolean isFollowing(User follower, UUID followingId) {
        return followRepository.existsByFollowerAndFollowing(follower, getUser(followingId));
    }

    public long getFollowersCount(UUID userId) {
        return followRepository.countByFollowing(getUser(userId));
    }

    public long getFollowingCount(UUID userId) {
        return followRepository.countByFollower(getUser(userId));
    }

    private User getUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
