package com.example.post.practice.post.controller;

import com.example.post.practice.jwt.SecurityUtil;
import com.example.post.practice.post.domain.dto.CreatePostDto;
import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.UpdatePostDto;
import com.example.post.practice.post.domain.entity.Post;
import com.example.post.practice.post.exception.NotPermissionException;
import com.example.post.practice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.invoke.MutableCallSite;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    // 게시물 전체 조회하기
    @GetMapping
    public ResponseEntity<Page<PostSummaryDto>> getAllPostSummaryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostSummaryDto> postDtoPage = postService.getAllPostSummaries(pageable);
        return new ResponseEntity<>(postDtoPage, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Boolean> createPost(@RequestPart("createPostDto") CreatePostDto createPostDto,
                                              @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        String memberId = SecurityUtil.getCurrentUsername();
        if(!memberId.equals("anonymousUser")) {
            String filename = postService.saveImage(multipartFile);
            postService.createPost(filename, createPostDto,memberId);
            return ResponseEntity.ok(true);
        }else{
            throw new NotPermissionException("권한이 없습니다.");
        }
    }
    // 상세조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> viewPost(@PathVariable Long postId){
        PostDto postDto = postService.getPost(postId);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    // 좋아요 누르기
    @PostMapping("/{postId}/like")
    public void likePost(@PathVariable Long postId){
        String userId = SecurityUtil.getCurrentUsername();
        if(userId.equals("anonymousUser")){
            throw new NotPermissionException("권한이 없습니다.");
        }else{
            postService.likePlus(postId);
        }
    }

    // 게시물 수정하기
    @PatchMapping("/{postId}")
    public void updatePost(@PathVariable Long postId, @RequestPart UpdatePostDto updatePostDto,
                           @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        String userId = SecurityUtil.getCurrentUsername();
        PostDto postDto = postService.getPost(postId);
        if(userId.equals(postDto.getMemberId())){
            if(multipartFile != null){
                postService.updateImage(postId,multipartFile);
            }
            postService.updatePost(postId, updatePostDto);
        }else{
            throw new NotPermissionException("수정할 권한이 없습니다.");
        }
    }

    // 게시물 삭제하기
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId){
        String userId = SecurityUtil.getCurrentUsername();
        PostDto postDto = postService.getPost(postId);
        if(userId.equals(postDto.getMemberId())){
            postService.deletePost(postId);
        }else{
            throw new NotPermissionException("삭제할 권한이 없습니다.");
        }
    }

}
