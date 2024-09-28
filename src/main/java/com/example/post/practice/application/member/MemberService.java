package com.example.post.practice.application.member;

import com.example.post.practice.security.JwtToken;

public interface MemberService {
    JwtToken signIn(String username, String password);
}
