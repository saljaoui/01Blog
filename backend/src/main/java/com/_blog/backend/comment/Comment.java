package com._blog.backend.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com._blog.backend.like.Like;
import com._blog.backend.post.Post;
import com._blog.backend.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;

        // Cascade relationships for automatic deletion
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes;
}   
