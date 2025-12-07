package com.school.StudyShare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean; // 삭제됨
// import org.springframework.web.servlet.config.annotation.CorsRegistry; // 삭제됨
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 삭제됨

@SpringBootApplication
public class StudyShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyShareApplication.class, args);
    }

    // 기존의 corsConfigurer() 메서드가 완전히 제거되었습니다.
    // CORS 설정은 SecurityConfig.java에서만 처리됩니다.
}