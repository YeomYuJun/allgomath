package com.yy.allgomath.sort.algorithm;

import com.yy.allgomath.sort.model.SwapOperation;
import com.yy.allgomath.sort.model.TrackingElement;

import java.util.ArrayList;
import java.util.List;

/**
 * 힙 정렬 알고리즘 구현
 */
public class HeapSort {
    private List<SwapOperation> swapLog;
    private int globalStep;

    public HeapSort() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    public void reset() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    /**
     * 힙 정렬 알고리즘을 시작합니다.
     * 
     * @param arr 정렬할 배열
     */
    public void sort(TrackingElement[] arr) {
        int n = arr.length;

        // 최대 힙 구축 (Build Max Heap)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
            globalStep++;
        }

        // 힙에서 요소를 하나씩 추출
        for (int i = n - 1; i > 0; i--) {
            // 루트(최대값)를 배열의 마지막 위치로 이동
            swap(arr, 0, i, "최대값(루트)을 마지막 위치로 이동");
            
            // 루트를 제외한 나머지에 대해 힙 구조 유지
            heapify(arr, i, 0);
            globalStep++;
        }
    }

    /**
     * 힙 구조를 유지하는 메서드
     * 
     * @param arr 배열
     * @param n 힙 크기
     * @param i 현재 노드 인덱스
     */
    private void heapify(TrackingElement[] arr, int n, int i) {
        int largest = i;         // 현재 노드를 최대값으로 초기화
        int left = 2 * i + 1;    // 왼쪽 자식
        int right = 2 * i + 2;   // 오른쪽 자식
        
        int partitionStep = 1;
        
        // 왼쪽 자식이 부모보다 크면 largest 갱신
        if (left < n && arr[left].getValue() > arr[largest].getValue()) {
            largest = left;
        }
        
        // 오른쪽 자식이 현재 최대값보다 크면 largest 갱신
        if (right < n && arr[right].getValue() > arr[largest].getValue()) {
            largest = right;
        }
        
        // largest가 루트가 아니면 교환하고 재귀적으로 heapify 호출
        if (largest != i) {
            swap(arr, i, largest, String.format("부모(%d)보다 자식(%d)이 더 큼, 위치 교환", 
                    arr[i].getValue(), arr[largest].getValue()));
            
            // 해당 서브트리에 대해 재귀적으로 heapify 수행
            heapify(arr, n, largest);
        }
    }
    
    /**
     * 배열의 두 요소를 교환하고 로그에 기록합니다.
     * 
     * @param arr 배열
     * @param i 첫 번째 인덱스
     * @param j 두 번째 인덱스
     * @param reason 교환 이유
     */
    private void swap(TrackingElement[] arr, int i, int j, String reason) {
        // 교환 작업 기록
        SwapOperation op = new SwapOperation(
                globalStep, 1, i, j,
                arr[i], arr[j],
                reason
        );
        swapLog.add(op);
        
        // 실제 요소 교환
        TrackingElement temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * 교환 작업 로그를 가져옵니다.
     * 
     * @return 교환 작업 로그
     */
    public List<SwapOperation> getSwapLog() {
        return swapLog;
    }

    /**
     * 현재 단계 번호를 가져옵니다.
     * 
     * @return 현재 단계 번호
     */
    public int getGlobalStep() {
        return globalStep;
    }
}
