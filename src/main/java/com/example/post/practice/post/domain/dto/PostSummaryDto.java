package com.example.post.practice.post.domain.dto;

import com.example.post.practice.post.domain.entity.Post;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PostSummaryDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long likeCount;

    public static List<PostSummaryDto> toPostSummaryDtos(List<Post> postList) {
        List<PostSummaryDto> postSummaryDtoList = new ArrayList<>();
        for (Post post : postList) {
            postSummaryDtoList.add(toPostSummaryDto(post));
        }
        return postSummaryDtoList;
    }

    public static PostSummaryDto toPostSummaryDto(Post post) {
        return PostSummaryDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(sliceContent(post.getContent()))
                .imageUrl(post.getImageUrl())
                .likeCount(post.getLikeCount())
                .build();
    }

    public static String sliceContent(String content) {
        if (content.length() > 30) {
            return content.substring(0, 30) + "...";
        } else {
            return content;
        }
    }
}
