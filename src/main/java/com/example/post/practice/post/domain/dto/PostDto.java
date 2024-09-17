package com.example.post.practice.post.domain.dto;

import com.example.post.practice.post.domain.entity.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {
    private Long id;
    private String memberId;
    private String title;
    private String content;
    private String imageUrl;
    private long likeCount;
    public static Post toEntity(PostDto postDto) {
        return Post.builder()
                .id(postDto.getId())
                .memberId(postDto.getMemberId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .imageUrl(postDto.getImageUrl())
                .likeCount(postDto.getLikeCount())
                .build();
    }
}
