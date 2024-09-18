package com.example.post.practice.services.post;

import com.example.post.practice.IntegrationTest;
import com.example.post.practice.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PostIntegrationTest extends IntegrationTest {
    @Autowired
    private PostRepository postRepository;

}

