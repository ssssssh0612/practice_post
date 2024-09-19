package com.example.post.practice.post.service;

import com.example.post.practice.post.config.ImgurConfig;
import com.example.post.practice.post.domain.dto.CreateOrUpdatePostDto;
import com.example.post.practice.post.domain.dto.ImageDto;
import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.entity.LikePost;
import com.example.post.practice.post.domain.entity.Post;
import com.example.post.practice.post.exception.PostNotFoundException;
import com.example.post.practice.post.repository.LikePostRepository;
import com.example.post.practice.post.repository.PostRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public PostDto createPost(ImageDto imageDto, @Valid CreateOrUpdatePostDto createPostDto, String memberId) {
        Post post = Post.builder()
                .imageUrl(imageDto.getImgUrl())
                .content(createPostDto.getContent())
                .title(createPostDto.getTitle())
                .memberId(memberId)
                .deleteHash(imageDto.getDeleteHash())
                .build();
        postRepository.save(post);
        return post.toDto();
    }

    @Override
    public void updatePost(Long postId, @Valid CreateOrUpdatePostDto updatePostDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        post.updateFromDto(updatePostDto);
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        post.deleteFromDto();
        deleteImage(post.getDeleteHash());
        postRepository.save(post);
    }

    @Override
    public long getPostCount() {
        return postRepository.count();
    }

    //TODO Url로 받아와서 변환
    @Override
    public ImageDto saveImage(MultipartFile multipartFile) throws IOException {
        // null이 좋을까 빈 문자열이 좋을까
        String imgurUrl = "";
        String deleteHash = "";
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
                deleteHash = dataNode.path("deletehash").asText();
            } else {
                throw new RuntimeException("이미지 업로드에 실패했습니다" + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다", e);
        }
        return ImageDto.builder()
                .imgUrl(imgurUrl)
                .deleteHash(deleteHash)
                .build();
    }


    //TODO Url로 받아와서 변환
    @Override
    public void updateImage(Long postId, MultipartFile multipartFile) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        // 이 전에 저장된 사진 삭제
        deleteImage(post.getDeleteHash());
        // 이미지를 저장해서 새로 받아옴
        ImageDto imageDto = saveImage(multipartFile);
        post.updateImageUrl(imageDto);
        postRepository.save(post);
    }

    @Override
    public void deleteImage(String deleteHash) throws IOException {
        String url = "https://api.imgur.com/3/image/" + deleteHash;
        // TODO set사용 없애기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = ImgurConfig.restTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("이미지가 성공적으로 삭제되었습니다.");
        } else {
            throw new RuntimeException("이미지 삭제에 실패했습니다." + response.getStatusCode());
        }
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

    @Override
    public void likePlusOrMinus(Long postId, String memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        if (likePostRepository.existsByPostIdAndMemberId(postId, memberId)) {
            LikePost likePost = likePostRepository.findByPostIdAndMemberId(postId, memberId);
            // 존재한다면 해당 컬럼을 찾은 후, deletedAt 의 값으로 확인하기
            if (likePost.getDeletedAt()) {
                likePost.deletedChecking();
                likePostRepository.save(likePost);
                post.plusLikeCount();
                postRepository.save(post);
            } else {
                likePost.deletedChecking();
                likePostRepository.save(likePost);
                post.plusLikeCount();
                postRepository.save(post);
            }
        } else {
            // 존재하지 않는다면 좋아요를 처음 누르는 경우
            LikePost likePost = new LikePost(postId, memberId);
            likePostRepository.save(likePost);
            post.plusLikeCount();
            postRepository.save(post);
        }
    }


    @Override
    public Long likeCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not Found"));
        return post.getLikeCount();
    }
}
