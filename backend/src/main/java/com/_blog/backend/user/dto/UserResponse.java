package com._blog.backend.user.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private boolean currentUser;
    private long followersCount;
    private long followingCount;
    private long postsCount;
    private String email;
    private String role;
}