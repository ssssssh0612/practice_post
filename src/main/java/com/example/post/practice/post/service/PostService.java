package com.example.post.practice.post.service;

import com.example.post.practice.post.domain.dto.CreatePostDto;
import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.UpdatePostDto;
import com.example.post.practice.post.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostDto createPost(String filename, CreatePostDto createPostDto, String memberId);
    void updatePost(Long postId, UpdatePostDto updatePostDto);
    void deletePost(Long postId);
    PostDto getPost(Long postId);
    Page<PostSummaryDto> getAllPostSummaries(Pageable pageable);
    long getPostCount();
    String saveImage(MultipartFile multipartFile) throws IOException;
    void updateImage(Long postId,MultipartFile multipartFile) throws IOException;
    void likePlus(Long postId);
    Long likeCount(Long postId);
}
