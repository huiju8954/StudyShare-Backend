package com.school.StudyShare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                // Flutter Web 포트 8080 및 고객님의 로컬 IP:8081 허용
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080", "http://192.168.199.1:8081")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 모든 HTTP 메서드 허용
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 인증 정보(쿠키) 허용
                .maxAge(3600); // 1시간 동안 Preflight 결과를 캐시
    }
}