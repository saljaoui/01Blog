package com._blog.backend.post;

import java.util.UUID;

import com._blog.backend.user.User;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "body_md", columnDefinition = "TEXT", nullable = false)
    private String bodyMd;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
