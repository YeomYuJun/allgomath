package com.yy.allgomath.service;

import com.yy.allgomath.datatype.QuickSortRequest;
import com.yy.allgomath.datatype.QuickSortResult;
import com.yy.allgomath.datatype.TrackingElement;
import com.yy.allgomath.util.algorithm.QuickSort;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;

@Service
public class QuickSortService {
    private final QuickSort quickSort;

    public QuickSortService() {
        this.quickSort = new QuickSort();
    }

    /**
     * 퀵 정렬 수행 메서드
     * 
     * @param request 퀵 정렬 요청 데이터
     * @return 퀵 정렬 결과
     */
    public QuickSortResult performQuickSort(QuickSortRequest request) {
        // 요청에 따라 배열 생성
        TrackingElement[] arr;

        if (request.hasCustomValues()) {
            // 사용자 정의 값으로 배열 생성
            Integer[] customValues = request.getCustomValues();
            arr = new TrackingElement[customValues.length];

            for (int i = 0; i < customValues.length; i++) {
                arr[i] = new TrackingElement(customValues[i], i + 1);
            }
        } else {
            // 랜덤 값으로 배열 생성
            Random random = new Random();
            int size = request.getArraySize();
            int min = request.getMinValue();
            int max = request.getMaxValue();

            arr = new TrackingElement[size];

            for (int i = 0; i < size; i++) {
                int value = random.nextInt(max - min + 1) + min;
                arr[i] = new TrackingElement(value, i + 1);
            }
        }

        // 정렬 수행
        TrackingElement[] originalArray = Arrays.copyOf(arr, arr.length);
        quickSort.sort(arr, 0, arr.length - 1);

        // 결과 반환
        return new QuickSortResult(arr, quickSort.getSwapLog(), quickSort.getGlobalStep() - 1);
    }
} 