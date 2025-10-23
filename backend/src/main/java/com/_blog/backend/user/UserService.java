package com._blog.backend.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.follow.Follow;
import com._blog.backend.follow.FollowRepository;
import com._blog.backend.post.PostRepository;
import com._blog.backend.role.Role;
import com._blog.backend.user.dto.UserRequest;
import com._blog.backend.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    public User createUser(UserRequest request) {
        
        Role role;
        if ("admin".equals(request.getUsername())) {
            role = Role.ADMIN;
        } else {
            role = Role.USER;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .bio("Sharing my learning journey and discoveries on 01Blog.")
                .password(request.getPassword())
                .id(UUID.randomUUID())
                .role(role)
                .build();
        userRepository.save(user);
        return user;
    }

    public UserResponse getUserProfileWithStats(User user) {
        long followersCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);
        long postsCount = postRepository.countByUser(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .bio(user.getBio())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .postsCount(postsCount)
                .build();
    }
}
