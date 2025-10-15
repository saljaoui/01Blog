package com._blog.backend.follow;

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

    public void follow(User follower, String followingId) {
        User following = getUser(followingId);
        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            followRepository.save(Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build());
        }
    }

    public void unfollow(User follower, String followingId) {
        User following = getUser(followingId);
        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

    public boolean isFollowing(User follower, String followingId) {
        return followRepository.existsByFollowerAndFollowing(follower, getUser(followingId));
    }

    public long getFollowersCount(String userId) {
        return followRepository.countByFollowing(getUser(userId));
    }

    public long getFollowingCount(String userId) {
        return followRepository.countByFollower(getUser(userId));
    }

    public List<User> getFollowers(String userId) {
        return followRepository.findAllByFollowing(getUser(userId))
                .stream().map(Follow::getFollower).toList();
    }

    public List<User> getFollowing(String userId) {
        return followRepository.findAllByFollower(getUser(userId))
                .stream().map(Follow::getFollowing).toList();
    }

    private User getUser(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
