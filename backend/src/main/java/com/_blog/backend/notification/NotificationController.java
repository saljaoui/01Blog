package com._blog.backend.notification;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.notification.dto.NotificationResponse;
import com._blog.backend.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        User user = SecurityUtils.getCurrentUser();
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(user.getUsername());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        User user = SecurityUtils.getCurrentUser();
        long count = notificationService.getUnreadCount(user.getUsername());
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        User user = SecurityUtils.getCurrentUser();
        notificationService.markAsRead(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleRead(@PathVariable Long id) {
        User user = SecurityUtils.getCurrentUser();
        notificationService.toggleRead(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

}
