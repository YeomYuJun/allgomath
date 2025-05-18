package com.yy.allgomath.controller;

import com.yy.allgomath.common.constants.AlgorithmConstants;
import com.yy.allgomath.datatype.SortRequest;
import com.yy.allgomath.datatype.SortResult;
import com.yy.allgomath.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 정렬 알고리즘 API 컨트롤러
 */
@RestController@RequestMapping("/api/algorithms/sorts")
public class SortController {
    private final SortService sortService;

    @Autowired
    public SortController(SortService sortService) {
        this.sortService = sortService;
    }

    /**
     * 지원되는 정렬 알고리즘 목록을 반환합니다.
     * 
     * @return ResponseEntity<Map<String, Object>> 지원되는 알고리즘 목록과 상태를 포함하는 응답
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
     * @param request 정렬 요청 데이터
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping
    public ResponseEntity<SortResult> sort(@RequestBody SortRequest request) {
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 퀵 정렬 요청을 처리합니다.
     *
     * @param request 정렬 요청 데이터
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping("/quick")
    public ResponseEntity<SortResult> quickSort(@RequestBody SortRequest request) {
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_QUICK);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 합병 정렬 요청을 처리합니다.
     *
     * @param request 정렬 요청 데이터
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping("/merge")
    public ResponseEntity<SortResult> mergeSort(@RequestBody SortRequest request) {
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_MERGE);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 힙 정렬 요청을 처리합니다.
     *
     * @param request 정렬 요청 데이터
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping("/heap")
    public ResponseEntity<SortResult> heapSort(@RequestBody SortRequest request) {
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_HEAP);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 랜덤 배열 생성 및 퀵 정렬 수행
     *
     * @param size 배열의 크기
     * @param minValue 배열의 최소값
     * @param maxValue 배열의 최대값
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/quick/random")
    public ResponseEntity<SortResult> randomQuickSort(
            @RequestParam int size,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        
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
     * 사용자 정의 배열로 퀵 정렬 수행
     *
     * @param values 사용자 정의 배열
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/quick/arr")
    public ResponseEntity<SortResult> customArrayQuickSort(@RequestParam Integer[] values) {
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
     *
     * @param size 배열의 크기
     * @param minValue 배열의 최소값
     * @param maxValue 배열의 최대값
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/merge/random")
    public ResponseEntity<SortResult> randomMergeSort(
            @RequestParam int size,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        
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
     *
     * @param values 사용자 정의 배열
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/merge/arr")
    public ResponseEntity<SortResult> customArrayMergeSort(@RequestParam Integer[] values) {
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
     *
     * @param size 배열의 크기
     * @param minValue 배열의 최소값
     * @param maxValue 배열의 최대값
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/heap/random")
    public ResponseEntity<SortResult> randomHeapSort(
            @RequestParam int size,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        
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
     *
     * @param values 사용자 정의 배열
     * @return ResponseEntity<SortResult> 정렬 결과와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/heap/arr")
    public ResponseEntity<SortResult> customArrayHeapSort(@RequestParam Integer[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_EMPTY_ARRAY);
        }

        SortRequest request = new SortRequest(values);
        request.setAlgorithm(AlgorithmConstants.SORT_ALGORITHM_HEAP);
        SortResult result = sortService.performSort(request);
        return ResponseEntity.ok(result);
    }
} 