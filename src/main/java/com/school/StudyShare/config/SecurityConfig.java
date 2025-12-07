// SecurityConfig.java

package com.school.StudyShare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.logout(Customizer.withDefaults());

        // ⭐️ /notes/** 및 /communities/** 경로를 인증 없이 접근 허용하도록 수정했습니다.
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/member/**").permitAll()
                .requestMatchers("/notes/**").permitAll()        // 💡 추가: 노트 API 경로 접근 허용
                .requestMatchers("/communities/**").permitAll()  // 💡 추가: 커뮤니티 API 경로 접근 허용
                .anyRequest().authenticated());

        return http.build();
    }

    // CORS 전역 필터: Flutter Web 포트 8080 허용
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://127.0.0.1:8080");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}