package com.example.post.practice.post.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrUpdatePostDto {
    @NotBlank(message = "제목이 공백일 수 없습니다.")
    private String title;
    @NotBlank(message = "내용이 공백일 수 없습니다.")
    private String content;
}
