package com.example.post.practice.post.service;

import com.example.post.practice.post.domain.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
    PostDto createPost(@Valid CreatePostDto createPostDto, String memberId);
    void updatePost(Long postId, @Valid UpdatePostDto updatePostDto);
    void deletePost(Long postId) throws IOException;
    PostDto getPost(Long postId);
    Page<PostSummaryDto> getAllPostSummaries(Pageable pageable);
    Long getPostCount();
    void likePlusOrMinus(Long postId,String memberId);
    Long likeCount(Long postId);
}
