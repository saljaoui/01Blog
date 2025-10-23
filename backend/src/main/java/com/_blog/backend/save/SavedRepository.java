package com._blog.backend.save;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog.backend.post.Post;
import com._blog.backend.user.User;

@Repository
public interface SavedRepository extends JpaRepository<Saved, Long> {
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
    long countByPost(Post post);
    java.util.List<Saved> findByUser(User user);
}
