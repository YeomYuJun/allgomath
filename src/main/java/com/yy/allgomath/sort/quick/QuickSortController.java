package com.yy.allgomath.sort.quick;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sort/quick")
public class QuickSortController {
    private final QuickSortService sortService;

    public QuickSortController() {
        this.sortService = new QuickSortService();
    }



    // 랜덤 배열로 정렬 요청 처리
    @GetMapping(value = "/random")
    public SortResult sortRandomArray(int size, int minValue, int maxValue) {
        // 파라미터 검증
        if (size < 1) {
            throw new IllegalArgumentException("배열 크기는 1 이상이어야 합니다.");
        }
        if (minValue > maxValue) {
            throw new IllegalArgumentException("최소값은 최대값보다 작거나 같아야 합니다.");
        }

        SortRequest request = new SortRequest(size, minValue, maxValue);
        return sortService.performQuickSort(request);
    }

    // 사용자 정의 배열로 정렬 요청 처리
    @GetMapping(value = "/arr")
    public SortResult sortCustomArray(Integer[] values) {
        // 파라미터 검증
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("배열은 비어있지 않아야 합니다.");
        }

        SortRequest request = new SortRequest(values);
        return sortService.performQuickSort(request);
    }
}
