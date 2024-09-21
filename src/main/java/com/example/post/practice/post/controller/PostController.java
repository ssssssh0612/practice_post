package com.example.post.practice.post.controller;

import com.example.post.practice.jwt.SecurityUtil;
import com.example.post.practice.post.domain.dto.ImageDto;
import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.CreateOrUpdatePostDto;
import com.example.post.practice.post.exception.NotPermissionException;
import com.example.post.practice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    // 게시물(요약) 전체 조회하기
    @GetMapping
    public ResponseEntity<Page<PostSummaryDto>> getAllPostSummaryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostSummaryDto> postDtoPage = postService.getAllPostSummaries(pageable);
        return new ResponseEntity<>(postDtoPage, HttpStatus.OK);
    }

    // 게시물 단일 상세조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> viewPost(@PathVariable Long postId) {
        PostDto postDto = postService.getPost(postId);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    // 게시물 만들기
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestPart("createPostDto") CreateOrUpdatePostDto createPostDto,
                                              @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        String memberId = SecurityUtil.getCurrentUsername();
        ImageDto imageDto = postService.saveImage(multipartFile);
        return ResponseEntity.ok(postService.createPost(imageDto, createPostDto, memberId));
    }

    // 좋아요 누르기, 취소하기
    @PostMapping("/{postId}/like")
    public ResponseEntity<Long> likePost(@PathVariable Long postId) {
        String memberId = SecurityUtil.getCurrentUsername();
        postService.likePlusOrMinus(postId, memberId);
        return ResponseEntity.ok(postService.likeCount(postId));
    }

    // 게시물 수정하기
    @PatchMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId, @RequestPart CreateOrUpdatePostDto updatePostDto,
                                              @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        String userId = SecurityUtil.getCurrentUsername();
        PostDto postDto = postService.getPost(postId);
        if (userId.equals(postDto.getMemberId())) {
            if (multipartFile != null) {
                postService.updateImage(postId, multipartFile);
            }
            postService.updatePost(postId, updatePostDto);
            return ResponseEntity.ok(postService.getPost(postId));
        } else {
            throw new NotPermissionException("수정할 권한이 없습니다.");
        }
    }

    // 게시물 삭제하기
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) throws IOException{
        String userId = SecurityUtil.getCurrentUsername();
        PostDto postDto = postService.getPost(postId);
        if (userId.equals(postDto.getMemberId())) {
            postService.deletePost(postId);
            return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
        } else {
            throw new NotPermissionException("삭제할 권한이 없습니다.");
        }
    }
}
