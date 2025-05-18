package com.yy.allgomath.controller;

import com.yy.allgomath.common.constants.AlgorithmConstants;
import com.yy.allgomath.service.QuickSortService;
import com.yy.allgomath.datatype.QuickSortRequest;
import com.yy.allgomath.datatype.QuickSortResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sort/quick")
public class QuickSortController {
    private final QuickSortService quickSortService;

    @Autowired
    public QuickSortController(QuickSortService quickSortService) {
        this.quickSortService = quickSortService;
    }

    // 랜덤 배열로 정렬 요청 처리
    @GetMapping(value = "/random")
    public ResponseEntity<QuickSortResult> sortRandomArray(
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

        QuickSortRequest request = new QuickSortRequest(size, minValue, maxValue);
        QuickSortResult result = quickSortService.performQuickSort(request);
        return ResponseEntity.ok(result);
    }

    // 사용자 정의 배열로 정렬 요청 처리
    @GetMapping(value = "/arr")
    public ResponseEntity<QuickSortResult> sortCustomArray(@RequestParam Integer[] values) {
        // 파라미터 검증
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(AlgorithmConstants.ERROR_EMPTY_ARRAY);
        }

        QuickSortRequest request = new QuickSortRequest(values);
        QuickSortResult result = quickSortService.performQuickSort(request);
        return ResponseEntity.ok(result);
    }
}
