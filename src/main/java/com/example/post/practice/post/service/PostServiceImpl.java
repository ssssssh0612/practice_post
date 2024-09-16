package com.example.post.practice.post.service;

import com.example.post.practice.post.config.ImgurConfig;
import com.example.post.practice.post.domain.dto.CreatePostDto;
import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.UpdatePostDto;
import com.example.post.practice.post.domain.entity.LikePost;
import com.example.post.practice.post.domain.entity.Post;
import com.example.post.practice.post.domain.mapper.PostMapper;
import com.example.post.practice.post.exception.PostNotFoundException;
import com.example.post.practice.post.repository.LikePostRepository;
import com.example.post.practice.post.repository.PostRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private static final String RELATIVE_PATH = "/db/post/";
    private static final String UPLOAD_DIR = System.getProperty("user.home") + RELATIVE_PATH;

    @Value("${my.api.key}")
    private String clientId;

    private final PostRepository postRepository;
    private final LikePostRepository likePostRepository;
    private final PostMapper postMapper;

    @Override
    public PostDto createPost(String filename, CreatePostDto createPostDto, String memberId) {
        Post post = Post.builder()
                .imageUrl(filename)
                .content(createPostDto.getContent())
                .title(createPostDto.getTitle())
                .memberId(memberId)
                .build();
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
        //TODO set사용 없애기
        postRepository.markPostAsDeleted(postId);
        postRepository.save(post);
    }

    @Override
    public long getPostCount() {
        return postRepository.count();
    }

    //TODO Url로 받아와서 변환
    @Override
    public String saveImage(MultipartFile multipartFile) throws IOException {
        String imgurUrl = null;
        try {
            String url = "https://api.imgur.com/3/image";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + clientId);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            });
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = ImgurConfig.restTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper om = new ObjectMapper();
                JsonNode jsonResponse = om.readTree(response.getBody());
                JsonNode dataNode = jsonResponse.path("data");
                imgurUrl = dataNode.path("link").asText();
            } else {
                throw new RuntimeException("Failed to upload image: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while uploading image", e);
        }
        return imgurUrl;
    }


    //TODO Url로 받아와서 변환
    @Override
    public void updateImage(Long postId, MultipartFile multipartFile) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        String imageUrl = post.getImageUrl();
        String newImageUrl = saveImage(multipartFile);
        Path imagePath = Paths.get(System.getProperty("user.home"), imageUrl);
        // 기존 이미지 삭제
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }
        post.setImageUrl(newImageUrl);
        postRepository.save(post);
    }

    @Override
    public Page<PostSummaryDto> getAllPostSummaries(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByDeletedAtFalse(pageable);
        List<Post> postListPage = postPage.getContent();
        List<PostSummaryDto> postSummaryDtoList = postMapper.toSummaryDtos(postListPage);
        return new PageImpl<>(postSummaryDtoList, pageable, postPage.getTotalElements());
    }

    @Override
    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return postMapper.toDto(post);
    }

    @Override
    //TODO set사용 없애기
    public void likePlusOrMinus(Long postId, String memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        if (likePostRepository.existsByPostIdAndMemberId(postId, memberId)) {
            LikePost likePost = likePostRepository.findByPostIdAndMemberId(postId, memberId);
            // 존재한다면 해당 컬럼을 찾은 후, deletedAt 의 값으로 확인하기
            if (likePost.getDeletedAt()) {
                likePost.setDeletedAt(Boolean.FALSE);
                likePostRepository.save(likePost);
                post.setLikeCount(post.getLikeCount() + 1);
                postRepository.save(post);
            } else {
                likePost.setDeletedAt(Boolean.TRUE);
                likePostRepository.save(likePost);
                post.setLikeCount(post.getLikeCount() - 1);
                postRepository.save(post);
            }
        } else {
            // 존재하지 않는다면 좋아요를 처음 누르는 경우
            LikePost likePost = new LikePost(postId, memberId);
            likePostRepository.save(likePost);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
        }
    }


    @Override
    public Long likeCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return post.getLikeCount();
    }
}
