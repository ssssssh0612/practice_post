package com.example.post.practice.member.service;

import com.example.post.practice.jwt.JwtToken;

public interface MemberService {
    public JwtToken signIn(String username, String password);
}
