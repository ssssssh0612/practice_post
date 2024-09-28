package com.example.post.practice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/h2-console").permitAll()
                                .requestMatchers(HttpMethod.POST,"/members/sign-in").permitAll()
                                .requestMatchers(HttpMethod.GET, "/posts").permitAll() // 게시물 전체조회
                                .requestMatchers(HttpMethod.GET, "/posts/*").permitAll() // 게시물 단일조회
                                .requestMatchers(HttpMethod.POST, "/posts").hasRole("USER") // 게시물 만들기
                                .requestMatchers(HttpMethod.PATCH, "/posts/*").hasRole("USER") // 게시물 수정
                                .requestMatchers(HttpMethod.DELETE, "/posts/*").hasRole("USER") // 게시물 삭제
                                .requestMatchers(HttpMethod.POST, "/posts/*/like").hasRole("USER") // 좋아요 누르기
                                .requestMatchers("/members/test").hasRole("USER")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        // HttpSecurity 객체를 빌드한 후 바로 반환
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}