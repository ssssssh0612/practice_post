package com.example.post.practice.member.service;

import com.example.post.practice.jwt.JwtToken;
import com.example.post.practice.jwt.JwtTokenProvider;
import com.example.post.practice.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;  // AuthenticationManager 주입

    @Transactional
    @Override
    public JwtToken signIn(String username, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 인증 처리
        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // JWT 토큰 생성
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }
}