package com.example.post.practice.application.post;

import com.example.post.practice.domain.dto.post.CreatePostDto;
import com.example.post.practice.domain.dto.post.PostDto;
import com.example.post.practice.domain.dto.post.PostSummaryDto;
import com.example.post.practice.domain.dto.post.UpdatePostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface PostService {
    PostDto createPost(CreatePostDto createPostDto, String memberId);
    void updatePost(Long postId, String userId, UpdatePostDto updatePostDto);
    void deletePost(Long postId) throws IOException;
    PostDto getPost(Long postId);
    Page<PostSummaryDto> getAllPostSummaries(Pageable pageable);
    Long getPostCount();
    void likePlusOrMinus(Long postId,String memberId);
    Long likeCount(Long postId);
}
