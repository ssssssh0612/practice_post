package com.example.post.practice.domain.entity.post;

import com.example.post.practice.domain.dto.post.PostDto;
import com.example.post.practice.domain.dto.post.PostSummaryDto;
import com.example.post.practice.domain.dto.post.UpdatePostDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // FK
    private String memberId;
    private String title;
    private String content;
    private String imageUrl;
    private Long likeCount;
    private Boolean deletedAt;

    @Builder
    public Post(String title, String content, String imageUrl, String memberId) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.memberId = memberId;
    }

    public PostDto toDto() {
        return PostDto.builder()
                .id(this.id)
                .memberId(this.memberId)
                .title(this.title)
                .content(this.content)
                .imageUrl(this.imageUrl)
                .likeCount(this.likeCount)
                .build();
    }

    public PostSummaryDto toPostSummaryDto() {
        return PostSummaryDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(sliceContent(this.getContent()))
                .imageUrl(this.getImageUrl())
                .likeCount(this.getLikeCount())
                .build();
    }

    public static String sliceContent(String content) {
        if (content.length() > 30) {
            return content.substring(0, 30) + "...";
        } else {
            return content;
        }
    }

    public void updatePost(UpdatePostDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.imageUrl = dto.getImageUrl();
    }

    public void deletePost(){
        this.deletedAt = true;
    }

    public void plusLikeCount(Long likeCount){
        this.likeCount = likeCount;
    }

    @PrePersist
    public void prePersist() {
        if (likeCount == null) {
            likeCount = 0L;
        }
        if (deletedAt == null || !deletedAt) {
            deletedAt = false;
        }
    }
}
