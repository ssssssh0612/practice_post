package com.example.post.practice.post.domain.mapper;

import com.example.post.practice.post.domain.dto.PostDto;
import com.example.post.practice.post.domain.dto.PostSummaryDto;
import com.example.post.practice.post.domain.dto.UpdatePostDto;
import com.example.post.practice.post.domain.entity.Post;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    List<Post> toEntities(List<PostDto> postDtos);
    List<PostDto> toDtos(List<Post> posts);

    List<PostDto> toDtos(Page<Post> posts);
    Post toEntity(PostDto postDto);
    PostDto toDto(Post post);

    @Mapping(target = "content", ignore = true) // content 필드를 무시하고 수동으로 설정
    PostSummaryDto toSummaryDto(Post post);

    List<PostSummaryDto> toSummaryDtos(List<Post> postList);

    // null로 올경우 무시하도록
    @Mapping(target = "title", source = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "content", source = "content", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPostDto(UpdatePostDto updatePostDto, @MappingTarget Post post);

    @AfterMapping
    default void truncateContent(@MappingTarget PostSummaryDto dto, Post post) {
        String content = post.getContent();
        if (content.length() > 30) {
            dto.setContent(content.substring(0, 30) + "...");
        } else {
            dto.setContent(content);
        }
    }
}
