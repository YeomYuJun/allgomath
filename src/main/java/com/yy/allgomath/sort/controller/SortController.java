package com.yy.allgomath.sort.controller;

import com.yy.allgomath.sort.model.SortRequest;
import com.yy.allgomath.sort.model.SortResult;
import com.yy.allgomath.sort.service.SortService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/algorithms/sorts")
public class SortController {
    private final SortService sortService;

    @Autowired
    public SortController(SortService sortService) {
        this.sortService = sortService;
    }

    /**
     * 지원되는 정렬 알고리즘 목록을 반환합니다.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSupportedAlgorithms() {
        Map<String, Object> response = new HashMap<>();
        response.put("algorithms", Arrays.asList("quick"));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * 정렬 요청을 처리합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    @PostMapping
    public ResponseEntity<SortResult> sort(@RequestBody SortRequest request) {
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 퀵 정렬 요청을 처리합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    @PostMapping("/quick")
    public ResponseEntity<SortResult> quickSort(@RequestBody SortRequest request) {
        // 알고리즘 타입을 퀵 정렬로 설정
        request.setAlgorithm("quick");
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 랜덤 배열 생성 및 퀵 정렬 수행 (기존 API와의 호환성을 위한 메서드)
     */
    @GetMapping("/quick/random")
    public ResponseEntity<SortResult> legacyRandomQuickSort(
            @RequestParam int size,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        
        // 파라미터 검증
        if (size < 1) {
            throw new IllegalArgumentException("배열 크기는 1 이상이어야 합니다.");
        }
        if (minValue > maxValue) {
            throw new IllegalArgumentException("최소값은 최대값보다 작거나 같아야 합니다.");
        }

        SortRequest request = new SortRequest(size, minValue, maxValue);
        request.setAlgorithm("quick");
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자 정의 배열로 퀵 정렬 수행 (기존 API와의 호환성을 위한 메서드)
     */
    @GetMapping("/quick/arr")
    public ResponseEntity<SortResult> legacyCustomArrayQuickSort(@RequestParam Integer[] values) {
        // 파라미터 검증
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("배열은 비어있지 않아야 합니다.");
        }

        SortRequest request = new SortRequest(values);
        request.setAlgorithm("quick");
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
}
