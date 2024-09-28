package com.example.post.practice.domain.dto.post;

import lombok.*;

@Getter
public class PostDto {
    private final Long id;
    private final String memberId;
    private final String title;
    private final String content;
    private final String imageUrl;
    private final Long likeCount;

    @Builder
    public PostDto(Long id, String memberId, String title, String content, String imageUrl, Long likeCount) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
    }
}
