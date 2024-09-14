package com.example.post.practice.post.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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

    public LikePost(Long postId, String memberId){
        this.postId = postId;
        this.memberId = memberId;
    }
}
