package com._blog.backend.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog.backend.user.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);

    long countByRecipientAndIsReadFalse(User recipient);
}
