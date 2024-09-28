package com.example.post.practice.infrastructure.repository.post;

import com.example.post.practice.domain.entity.post.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    boolean existsByPostIdAndMemberId(Long postId, String memberId);
    LikePost findByPostIdAndMemberId(Long postId, String memberId);
    Long countByPostIdAndDeletedAtFalse(Long postId);
}
