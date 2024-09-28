package com.example.post.practice.domain.entity.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class LikePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeIndex;
    private Long postId;
    private String memberId;
    private Boolean deletedAt;

    @PrePersist
    public void prePersist() {
        if (deletedAt == null || !deletedAt) {
            deletedAt = false;
        }
    }

    public LikePost(Long postId, String memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public void deletedChecking() {
        if (this.deletedAt) {
            this.deletedAt = false;
            return;
        }
        this.deletedAt = true;
    }
}
