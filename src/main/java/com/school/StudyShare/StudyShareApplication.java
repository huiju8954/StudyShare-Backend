package com.school.StudyShare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class StudyShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyShareApplication.class, args);
    }

    // 💡 [필수] 브라우저 보안(CORS) 해제 설정
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") // 모든 주소 허용
                        .allowedMethods("*")        // 모든 기능 허용
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}