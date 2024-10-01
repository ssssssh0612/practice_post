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
    private Boolean deletedAt;

    @Builder
    public Post(String title, String content, String imageUrl, String memberId) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.memberId = memberId;
        this.deletedAt = false;
    }

    public PostDto toCreatedto(){
        return PostDto.builder()
                .id(this.id)
                .memberId(this.memberId)
                .title(this.title)
                .content(this.content)
                .imageUrl(this.imageUrl)
                .build();
    }

    public PostDto toDto( Long likeCount ) {
        return PostDto.builder()
                .id(this.id)
                .memberId(this.memberId)
                .title(this.title)
                .content(this.content)
                .imageUrl(this.imageUrl)
                .likeCount(likeCount)
                .build();
    }

    public PostSummaryDto toPostSummaryDto( Long likeCount) {
        return PostSummaryDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(sliceContent(this.getContent()))
                .imageUrl(this.getImageUrl())
                .likeCount(likeCount)
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
}
