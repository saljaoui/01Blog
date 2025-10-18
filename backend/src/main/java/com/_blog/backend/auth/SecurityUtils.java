package com._blog.backend.auth;

import com._blog.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class SecurityUtils {
    public static UUID getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
    public static User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}