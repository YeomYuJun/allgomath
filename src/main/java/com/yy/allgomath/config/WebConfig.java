package com.yy.allgomath.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.imageio.ImageIO;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebConfig {

    @PostConstruct
    public void initImageIOPlugins() {
        // WebP 등 플러그인 이미지 포맷을 스캔
        ImageIO.scanForPlugins();
    }

    /**
     * 관리 포트(9099) 전용 체인. 없으면 아래 체인의 authenticated() 가 걸려 Prometheus 스크레이프가 403 이 된다.
     * 이 포트는 도커 네트워크 내부에서만 도달 가능해 네트워크 경계로 보호한다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        // actuator 본체는 9099 로 분리됨. 여기 허용 대상은 HealthPingController 뿐이다.
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 모든 Origin 허용으로 디버깅 (임시)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // configuration.setAllowedOrigins(Arrays.asList(
        //         "http://localhost:5173",        // 로컬 개발용
        //         "https://allgomath.com",        // 프로덕션 도메인
        //         "https://d2qbdf5fpx2lej.cloudfront.net", // CloudFront
        //         "https://api.allgomath.com"     // API 도메인 (혹시 모를 직접 접근)
        // ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        //configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 적용
        
        return source;
    }
}