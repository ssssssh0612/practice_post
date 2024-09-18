package com.example.post.practice.post.service;

import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.CreateOrUpdatePostDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
    PostDto createPost(String filename,@Valid CreateOrUpdatePostDto createPostDto, String memberId);
    void updatePost(Long postId, @Valid CreateOrUpdatePostDto createOrUpdatePostDto);
    void deletePost(Long postId);
    PostDto getPost(Long postId);
    Page<PostSummaryDto> getAllPostSummaries(Pageable pageable);
    long getPostCount();
    String saveImage(MultipartFile multipartFile) throws IOException;
    void updateImage(Long postId,MultipartFile multipartFile) throws IOException;
    void likePlusOrMinus(Long postId,String memberId);
    Long likeCount(Long postId);
}
