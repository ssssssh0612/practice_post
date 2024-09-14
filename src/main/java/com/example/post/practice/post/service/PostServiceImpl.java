package com.example.post.practice.post.service;

import com.example.post.practice.post.domain.dto.CreatePostDto;
import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.UpdatePostDto;
import com.example.post.practice.post.domain.entity.Post;
import com.example.post.practice.post.domain.mapper.PostMapper;
import com.example.post.practice.post.exception.PostNotFoundException;
import com.example.post.practice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private static final String RELATIVE_PATH = "/db/post/";
    private static final String UPLOAD_DIR = System.getProperty("user.home") + RELATIVE_PATH;

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public PostDto createPost(String filename, CreatePostDto createPostDto, String memberId) {
        Post post = new Post();
        post.setImageUrl(filename);
        post.setContent(createPostDto.getContent());
        post.setTitle(createPostDto.getTitle());
        post.setMemberId(memberId);
        postRepository.save(post);
        return postMapper.toDto(post);
    }
    @Override
    public void updatePost(Long postId, UpdatePostDto updatePostDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        postMapper.updateFromPostDto(updatePostDto, post);
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        post.setDeletedAt(false);
        postRepository.save(post);
    }

    @Override
    public long getPostCount() {
        return postRepository.count();
    }

    @Override
    public String saveImage(MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = String.valueOf(UUID.randomUUID());
        Path filePath = uploadPath.resolve(fileName + ".png");
        Files.copy(multipartFile.getInputStream(), filePath);
        Path relativePath = Paths.get(RELATIVE_PATH);
        return relativePath.resolve(fileName + ".png").toString();
    }

    @Override
    public void updateImage(Long postId,MultipartFile multipartFile) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        String imageUrl = post.getImageUrl();
        String newImageUrl = saveImage(multipartFile);
        Path imagePath = Paths.get(System.getProperty("user.home"),imageUrl);
        // 기존 이미지 삭제
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }
        post.setImageUrl(newImageUrl);
        postRepository.save(post);
    }

    @Override
    public Page<PostSummaryDto> getAllPostSummaries(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByDeletedAtTrue(pageable);
        List<Post> postListPage = postPage.getContent();
        List<PostSummaryDto> postSummaryDtoList = postMapper.toSummaryDtos(postListPage);
        return new PageImpl<>(postSummaryDtoList, pageable, postPage.getTotalElements());
    }

    @Override
    public PostDto getPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return postMapper.toDto(post);
    }

    @Override
    public void likePlus(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        post.setLikeCount(post.getLikeCount()+1);
        postRepository.save(post);
    }

    @Override
    public Long likeCount(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return post.getLikeCount();
    }
}
