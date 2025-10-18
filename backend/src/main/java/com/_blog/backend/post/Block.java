package com._blog.backend.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blocks")
public class Block {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BlockType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String data; // JSON string for that block’s data (like text or image URL)

    private int position;
}
