package com.example.post.practice.post.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // FK
    private String memberId;
    private String title;
    private String content;
    private String imageUrl;
    private Long likeCount;
    private Boolean deletedAt;

    @PrePersist
    public void prePersist() {
        if (likeCount == null) {
            likeCount = 0L;
        }
        if (deletedAt == null || !deletedAt) {
            deletedAt = true;
        }
    }
}
