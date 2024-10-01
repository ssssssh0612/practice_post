package com.example.post.practice.presentation.post;

import com.example.post.practice.domain.dto.post.CreatePostDto;
import com.example.post.practice.domain.dto.post.PostDto;
import com.example.post.practice.domain.dto.post.PostSummaryDto;
import com.example.post.practice.domain.dto.post.UpdatePostDto;
import com.example.post.practice.security.SecurityUtil;
import com.example.post.practice.application.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Validated
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
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostDto createPostDto){
        String memberId = SecurityUtil.getCurrentUsername();
        return ResponseEntity.ok(postService.createPost(createPostDto, memberId));
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
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId, @Valid @RequestBody UpdatePostDto updatePostDto){
        String userId = SecurityUtil.getCurrentUsername();
        postService.updatePost(postId, userId, updatePostDto);
        return ResponseEntity.ok(postService.getPost(postId));
    }

    // 게시물 삭제하기
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) throws IOException{
        String userId = SecurityUtil.getCurrentUsername();
        postService.deletePost(postId,userId);
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }
}
