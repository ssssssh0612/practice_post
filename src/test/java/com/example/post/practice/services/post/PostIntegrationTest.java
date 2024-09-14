package com.example.post.practice.services.post;

import com.example.post.practice.IntegrationTest;
import com.example.post.practice.post.domain.mapper.PostMapper;
import com.example.post.practice.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

public class PostIntegrationTest extends IntegrationTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;


}

