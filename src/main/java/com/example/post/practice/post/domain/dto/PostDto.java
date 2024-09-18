package com.example.post.practice.post.domain.dto;

import com.example.post.practice.post.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String memberId;
    private String title;
    private String content;
    private String imageUrl;
    private long likeCount;

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
