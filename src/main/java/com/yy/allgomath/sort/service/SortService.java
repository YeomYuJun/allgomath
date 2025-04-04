package com.yy.allgomath.sort.service;

import com.yy.allgomath.common.constants.AlgorithmConstants;
import com.yy.allgomath.sort.algorithm.HeapSort;
import com.yy.allgomath.sort.algorithm.MergeSort;
import com.yy.allgomath.sort.algorithm.QuickSort;
import com.yy.allgomath.sort.model.SortRequest;
import com.yy.allgomath.sort.model.SortResult;
import com.yy.allgomath.sort.model.TrackingElement;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;

@Service
public class SortService {
    
    /**
     * 정렬 요청을 처리합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    public SortResult performSort(SortRequest request) {
        String algorithm = request.getAlgorithm();
        
        // 알고리즘에 따른 정렬 수행
        if (AlgorithmConstants.SORT_ALGORITHM_QUICK.equals(algorithm)) {
            return performQuickSort(request);
        } else if (AlgorithmConstants.SORT_ALGORITHM_MERGE.equals(algorithm)) {
            return performMergeSort(request);
        } else if (AlgorithmConstants.SORT_ALGORITHM_HEAP.equals(algorithm)) {
            return performHeapSort(request);
        } else {
            throw new IllegalArgumentException("지원하지 않는 정렬 알고리즘입니다: " + algorithm);
        }
    }
    
    /**
     * 요청에 따라 정렬할 배열을 생성합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 생성된 TrackingElement 배열
     */
    private TrackingElement[] createArray(SortRequest request) {
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
        
        return arr;
    }
    
    /**
     * 퀵 정렬을 수행합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    public SortResult performQuickSort(SortRequest request) {
        // 배열 생성
        TrackingElement[] arr = createArray(request);

        // 정렬 수행
        QuickSort quickSort = new QuickSort();
        TrackingElement[] originalArray = Arrays.copyOf(arr, arr.length);
        quickSort.sort(arr, 0, arr.length - 1);

        // 결과 반환
        return new SortResult(arr, quickSort.getSwapLog(), quickSort.getGlobalStep() - 1, AlgorithmConstants.SORT_ALGORITHM_QUICK);
    }
    
    /**
     * 합병 정렬을 수행합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    public SortResult performMergeSort(SortRequest request) {
        // 배열 생성
        TrackingElement[] arr = createArray(request);

        // 정렬 수행
        MergeSort mergeSort = new MergeSort();
        TrackingElement[] originalArray = Arrays.copyOf(arr, arr.length);
        mergeSort.sort(arr);

        // 결과 반환
        return new SortResult(arr, mergeSort.getMergeLog(), mergeSort.getGlobalStep() - 1, AlgorithmConstants.SORT_ALGORITHM_MERGE);
    }
    
    /**
     * 힙 정렬을 수행합니다.
     * 
     * @param request 정렬 요청 정보
     * @return 정렬 결과
     */
    public SortResult performHeapSort(SortRequest request) {
        // 배열 생성
        TrackingElement[] arr = createArray(request);

        // 정렬 수행
        HeapSort heapSort = new HeapSort();
        TrackingElement[] originalArray = Arrays.copyOf(arr, arr.length);
        heapSort.sort(arr);

        // 결과 반환
        return new SortResult(arr, heapSort.getSwapLog(), heapSort.getGlobalStep() - 1, AlgorithmConstants.SORT_ALGORITHM_HEAP);
    }
}
