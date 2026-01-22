package com.AIoT.Back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Rest API라 CSRF 끔
                .cors(cors -> cors.configure(http)) // 웹페이지로 실행함으로 cors 설정(보안)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/student/**").permitAll() // 로그인, 가입은 허용
                        .requestMatchers("/ws-stomp/**").permitAll() // 소켓 연결 허용
                        .requestMatchers("api/teacher/**").permitAll() // 선생님 전용 API 보호
                        .anyRequest().authenticated() // 나머지는 무조건 인증 필요
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)); // 세션 방식 사용시
        return http.build();
    }



}
