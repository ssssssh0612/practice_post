package com.example.post.practice.post.domain.dto;

import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private String memberId;
    private String title;
    private String content;
    private String imageUrl;
    private long likeCount;
}
