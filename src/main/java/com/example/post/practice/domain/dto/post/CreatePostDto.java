package com.example.post.practice.domain.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreatePostDto {
    @NotBlank(message = "제목이 공백일 수 없습니다.")
    private String title;
    @NotBlank(message = "내용이 공백일 수 없습니다.")
    private String content;
    private String imageUrl;
}