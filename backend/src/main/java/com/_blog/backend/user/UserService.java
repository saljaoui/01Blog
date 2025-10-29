package com._blog.backend.user;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.follow.FollowRepository;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.dto.UpdateProfileRequest;
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
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        return user;
    }

    public UserResponse getUserProfileWithStats(User user) {
        long followersCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);
        long postsCount = postRepository.countByUser(user);
        User currentUser = SecurityUtils.getCurrentUser();
        boolean isCurrentUser = currentUser != null && currentUser.getId().equals(user.getId());


        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .bio(user.getBio())
                .currentUser(isCurrentUser)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .postsCount(postsCount)
                .build();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public User updateProfile(User user, UpdateProfileRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        return userRepository.save(user);
    }
}
