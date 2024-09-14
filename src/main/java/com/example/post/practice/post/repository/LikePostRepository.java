package com.example.post.practice.post.repository;

import com.example.post.practice.post.domain.entity.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    boolean existsByPostIdAndMemberId(Long postId, String memberId);
    LikePost findByPostIdAndMemberId(Long postId, String memberId);
}
