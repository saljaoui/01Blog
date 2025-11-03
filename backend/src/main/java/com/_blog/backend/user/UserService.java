package com._blog.backend.user;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.exception.ResourceNotFoundException;
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
    private static final String UPLOAD_FOLDER_AVATARS = "uploads/avatars/";

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
                .avatarUrl("http://localhost:8080/api/users/avatars/default-avatar.png")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
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
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .currentUser(isCurrentUser)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .postsCount(postsCount)
                .createdAt(user.getCreatedAt())
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

    public User updateProfileWithAvatar(User user, UpdateProfileRequest request, MultipartFile avatar) {
        // Update basic info
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());

        // Handle avatar upload - SAME LOGIC AS POSTS
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // 1. Create folder if doesn't exist
                File uploadDir = new File(UPLOAD_FOLDER_AVATARS);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 2. Create unique filename
                String originalName = avatar.getOriginalFilename();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String newFileName = user.getId() + "_" + System.currentTimeMillis() + extension;

                // 3. Save file to disk
                Path filePath = Paths.get(UPLOAD_FOLDER_AVATARS + newFileName);
                Files.write(filePath, avatar.getBytes());

                // 4. Create public URL for the avatar
                String avatarUrl = "http://localhost:8080/api/users/avatars/" + newFileName;

                // 5. Save URL to user
                user.setAvatarUrl(avatarUrl);

                System.out.println("Avatar uploaded successfully: " + avatarUrl);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
            }
        }

        return userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::getUserProfileWithStats)
                .toList();
    }

    public UserResponse toggleUserStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if (user.getStatus().equals(UserStatus.BANNED)) {
            user.setStatus(UserStatus.ACTIVE);
        } else {
            user.setStatus(UserStatus.BANNED);
        }

        userRepository.save(user);

        return getUserProfileWithStats(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        userRepository.delete(user);
    }
}
