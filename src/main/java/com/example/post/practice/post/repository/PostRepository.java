package com.example.post.practice.post.repository;

import com.example.post.practice.member.domain.entity.Member;
import com.example.post.practice.post.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByDeletedAtFalse(Pageable pageable);
}
