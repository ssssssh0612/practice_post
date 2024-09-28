package com.example.post.practice.domain.dto.post;

import lombok.*;

@Getter
public class PostSummaryDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String imageUrl;
    private final Long likeCount;

    @Builder
    public PostSummaryDto(Long id, String title, String content, String imageUrl, Long likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
    }
}
