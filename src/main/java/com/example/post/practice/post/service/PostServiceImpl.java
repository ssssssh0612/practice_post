package com.example.post.practice.post.service;

import com.example.post.practice.post.domain.dto.*;
import com.example.post.practice.post.domain.entity.LikePost;
import com.example.post.practice.post.domain.entity.Post;
import com.example.post.practice.post.exception.PostNotFoundException;
import com.example.post.practice.post.repository.LikePostRepository;
import com.example.post.practice.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Validated
@Service
public class PostServiceImpl implements PostService {
    @Value("${my.api.key}")
    private String clientId;

    private final PostRepository postRepository;
    private final LikePostRepository likePostRepository;

    @Override
    public PostDto createPost(@Valid CreatePostDto createPostDto, String memberId) {
        Post post = Post.builder()
                .content(createPostDto.getContent())
                .title(createPostDto.getTitle())
                .memberId(memberId)
                .imageUrl(createPostDto.getImageUrl())
                .build();
        postRepository.save(post);
        return post.toDto();
    }

    @Transactional
    @Override
    public void updatePost(Long postId, @Valid UpdatePostDto updatePostDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        post.updatePost(updatePostDto);
    }

    @Transactional
    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        post.deletePost();
    }

    @Override
    public Long getPostCount() {
        return postRepository.count();
    }

    @Override
    public Page<PostSummaryDto> getAllPostSummaries(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByDeletedAtFalse(pageable);
        List<Post> postListPage = postPage.getContent();
        List<PostSummaryDto> postSummaryDtoList = new ArrayList<>();
        for (Post post : postListPage) {
            postSummaryDtoList.add(post.toPostSummaryDto());
        }
        return new PageImpl<>(postSummaryDtoList, pageable, postPage.getTotalElements());
    }

    @Override
    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return post.toDto();
    }

    @Transactional
    @Override
    public void likePlusOrMinus(Long postId, String memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        // 만약 존재한다면
        LikePost likePost = likePostRepository.findByPostIdAndMemberId(postId, memberId);
        if (likePost != null && !likePost.getDeletedAt()) {
            likePlusPost(likePost, post);
            return;
        }
        if (likePost != null) {
            likeMinusPost(likePost, post);
            return;
        }
        // 존재하지 않는다면
        handleNewLikePost(postId, memberId, post);
    }

    @Override
    public Long likeCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return post.getLikeCount();
    }

    private void likePlusPost(LikePost likePost, Post post) {
        likePost.deletedChecking();
        post.plusLikeCount();
    }
    private void likeMinusPost(LikePost likePost, Post post) {
        likePost.deletedChecking();
        post.minusLikeCount();
    }

    private void handleNewLikePost(Long postId, String memberId, Post post) {
        LikePost likePost = new LikePost(postId, memberId);
        likePostRepository.save(likePost);
        post.plusLikeCount();
    }
}
