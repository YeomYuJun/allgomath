package com.yy.allgomath.sort.controller;

import com.yy.allgomath.common.constants.AlgorithmConstants;
import com.yy.allgomath.sort.model.SortRequest;
import com.yy.allgomath.sort.model.SortResult;
import com.yy.allgomath.sort.service.SortService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 정렬 알고리즘 API 컨트롤러
 * 
 * API 사용 예시:
 * 
 * 1. 모든 정렬 알고리즘 조회
 * GET /api/algorithms/sorts
 * 
 * 2. POST 방식 API 호출 (JSON)
 * 
 * 2.1 퀵 정렬
 * POST /api/algorithms/sorts/quick
 * Content-Type: application/json
 * {"customValues": [9, 5, 7, 1, 3, 8, 2, 6, 4], "algorithm": "quick"}
 * 
 * 2.2 합병 정렬
 * POST /api/algorithms/sorts/merge
 * Content-Type: application/json
 * {"customValues": [9, 5, 7, 1, 3, 8, 2, 6, 4], "algorithm": "merge"}
 * 
 * 2.3 힙 정렬
 * POST /api/algorithms/sorts/heap
 * Content-Type: application/json
 * {"customValues": [9, 5, 7, 1, 3, 8, 2, 6, 4], "algorithm": "heap"}
 * 
 * 3. GET 방식 API 호출 (쿼리 파라미터)
 * 
 * 3.1 퀵 정렬
 * GET /api/algorithms/sorts/quick/random?size=10&minValue=1&maxValue=100
 * GET /api/algorithms/sorts/quick/arr?values=9,5,7,1,3,8,2,6,4
 * 
 * 3.2 합병 정렬
 * GET /api/algorithms/sorts/merge/random?size=10&minValue=1&maxValue=100
 * GET /api/algorithms/sorts/merge/arr?values=9,5,7,1,3,8,2,6,4
 * 
 * 3.3 힙 정렬
 * GET /api/algorithms/sorts/heap/random?size=10&minValue=1&maxValue=100
 * GET /api/algorithms/sorts/heap/arr?values=9,5,7,1,3,8,2,6,4
 */
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
        response.put("algorithms", Arrays.asList(
            AlgorithmConstants.SORT_ALGORITHM_QUICK, 
            AlgorithmConstants.SORT_ALGORITHM_MERGE,
            AlgorithmConstants.SORT_ALGORITHM_HEAP
        ));
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
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_QUICK);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 합병 정렬 요청을 처리합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    @PostMapping("/merge")
    public ResponseEntity<SortResult> mergeSort(@RequestBody SortRequest request) {
        // 알고리즘 타입을 합병 정렬로 설정
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_MERGE);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 힙 정렬 요청을 처리합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    @PostMapping("/heap")
    public ResponseEntity<SortResult> heapSort(@RequestBody SortRequest request) {
        // 알고리즘 타입을 힙 정렬로 설정
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_HEAP);
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
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_INVALID_ARRAY_SIZE);
        }
        if (minValue > maxValue) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_INVALID_VALUE_RANGE);
        }

        SortRequest request = new SortRequest(size, minValue, maxValue);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_QUICK);
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
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_EMPTY_ARRAY);
        }

        SortRequest request = new SortRequest(values);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_QUICK);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 랜덤 배열 생성 및 합병 정렬 수행
     */
    @GetMapping("/merge/random")
    public ResponseEntity<SortResult> randomMergeSort(
            @RequestParam int size,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        
        // 파라미터 검증
        if (size < 1) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_INVALID_ARRAY_SIZE);
        }
        if (minValue > maxValue) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_INVALID_VALUE_RANGE);
        }

        SortRequest request = new SortRequest(size, minValue, maxValue);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_MERGE);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자 정의 배열로 합병 정렬 수행
     */
    @GetMapping("/merge/arr")
    public ResponseEntity<SortResult> customArrayMergeSort(@RequestParam Integer[] values) {
        // 파라미터 검증
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_EMPTY_ARRAY);
        }

        SortRequest request = new SortRequest(values);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_MERGE);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 랜덤 배열 생성 및 힙 정렬 수행
     */
    @GetMapping("/heap/random")
    public ResponseEntity<SortResult> randomHeapSort(
            @RequestParam int size,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        
        // 파라미터 검증
        if (size < 1) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_INVALID_ARRAY_SIZE);
        }
        if (minValue > maxValue) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_INVALID_VALUE_RANGE);
        }

        SortRequest request = new SortRequest(size, minValue, maxValue);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_HEAP);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자 정의 배열로 힙 정렬 수행
     */
    @GetMapping("/heap/arr")
    public ResponseEntity<SortResult> customArrayHeapSort(@RequestParam Integer[] values) {
        // 파라미터 검증
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_EMPTY_ARRAY);
        }

        SortRequest request = new SortRequest(values);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_HEAP);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
}
