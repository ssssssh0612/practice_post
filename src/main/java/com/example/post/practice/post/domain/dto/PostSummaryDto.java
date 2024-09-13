package com.example.post.practice.post.domain.dto;

import lombok.Data;

@Data
public class PostSummaryDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long likeCount;
}
