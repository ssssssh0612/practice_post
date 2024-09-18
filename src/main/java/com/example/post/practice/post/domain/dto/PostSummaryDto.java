package com.example.post.practice.post.domain.dto;

import com.example.post.practice.post.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostSummaryDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long likeCount;

    @Builder
    public PostSummaryDto(Long id, String title, String content, String imageUrl, Long likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
    }
}
