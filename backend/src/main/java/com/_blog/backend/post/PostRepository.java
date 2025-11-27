package com._blog.backend.post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog.backend.user.User;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByUser(User user);
    List<Post> findByUserIn(List<User> users, Pageable pageable);
    long countByUser(User user);
}
