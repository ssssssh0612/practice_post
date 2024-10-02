package com.example.post.practice.application.post;

import com.example.post.practice.application.exception.InvalidUserIdException;
import com.example.post.practice.domain.dto.post.CreatePostDto;
import com.example.post.practice.domain.dto.post.PostDto;
import com.example.post.practice.domain.dto.post.PostSummaryDto;
import com.example.post.practice.domain.dto.post.UpdatePostDto;
import com.example.post.practice.domain.entity.post.LikePost;
import com.example.post.practice.domain.entity.post.Post;
import com.example.post.practice.domain.exception.PostNotFoundException;
import com.example.post.practice.infrastructure.repository.post.LikePostRepository;
import com.example.post.practice.infrastructure.repository.post.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    @Value("${my.api.key}")
    private String clientId;

    private final PostRepository postRepository;
    private final LikePostRepository likePostRepository;

    @Override
    public PostDto createPost(CreatePostDto createPostDto, String memberId) {
        Post post = Post.builder()
                .content(createPostDto.getContent())
                .title(createPostDto.getTitle())
                .memberId(memberId)
                .imageUrl(createPostDto.getImageUrl())
                .build();
        postRepository.save(post);
        return post.toCreatedto();
    }

    @Transactional
    @Override
    public void updatePost(Long postId, String userId, UpdatePostDto updatePostDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));
        if (!userId.equals(post.getMemberId())) {
            throw new InvalidUserIdException("아이디가 유효하지 않습니다");
        }
        post.updatePost(updatePostDto);
    }

    @Transactional
    @Override
    public void deletePost(Long postId, String userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));
        if(!userId.equals(post.getMemberId())) {
            throw new InvalidUserIdException("아이디가 유효하지 않습니다.");
        }
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
            postSummaryDtoList.add(post.toPostSummaryDto(likePostRepository.countByPostIdAndDeletedAtFalse(post.getId())));
        }
        return new PageImpl<>(postSummaryDtoList, pageable, postPage.getTotalElements());
    }

    @Override
    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));
        return post.toDto(getLikeCount(post.getId()));
    }

    @Transactional
    @Override
    public void toggleLikePostStatus(Long postId, String memberId) {
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));
        // 만약 존재한다면
        LikePost likePost = likePostRepository.findByPostIdAndMemberId(postId, memberId);
        if (likePost != null ) {
            likePost.isDeletedToggle();
            return;
        }
        // 존재하지 않는다면
        LikePost newLikePost = new LikePost(postId, memberId);
        likePostRepository.save(newLikePost);
    }

    // 좋아요 개수 반환하기
    @Override
    public Long getLikeCount(Long postId){
        return likePostRepository.countByPostIdAndDeletedAtFalse(postId);
    }
}
