package com.yy.allgomath.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 기본 API 엔드포인트를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * API 연결 테스트를 위한 엔드포인트
     * 
     * @return 테스트 응답 데이터
     */
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Spring Boot API 연동 성공!");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * 앱 버전 및 API 정보를 제공하는 엔드포인트
     * 
     * @return API 정보
     */
    @GetMapping("/info")
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "AllgoMath API");
        info.put("version", "1.0.0");
        info.put("description", "수학적 알고리즘과 시각화를 위한 RESTful API");
        
        // 주요 엔드포인트 목록
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("정렬 알고리즘", "/api/algorithms/sorts");
        endpoints.put("수학 함수", "/api/math/functions");
        
        info.put("endpoints", endpoints);
        info.put("timestamp", System.currentTimeMillis());
        return info;
    }
}
