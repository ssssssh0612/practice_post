package com.example.post.practice.post.domain.entity;

import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.CreateOrUpdatePostDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
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

    public void updateFromDto(CreateOrUpdatePostDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public void deleteFromDto(){
        this.deletedAt = true;
    }

    public void plusLikeCount(){
        this.likeCount++;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
