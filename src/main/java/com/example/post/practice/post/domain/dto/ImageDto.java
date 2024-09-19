package com.example.post.practice.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDto {
    private String deleteHash;
    private String imgUrl;
}
