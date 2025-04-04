package com.yy.allgomath.sort.algorithm;

import com.yy.allgomath.sort.model.SwapOperation;
import com.yy.allgomath.sort.model.TrackingElement;

import java.util.ArrayList;
import java.util.List;

public class QuickSort {
    private List<SwapOperation> swapLog;
    private int globalStep;

    public QuickSort() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    public void reset() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    /**
     * 배열을 퀵 정렬 알고리즘으로 정렬합니다.
     * 
     * @param arr 정렬할 배열
     * @param low 시작 인덱스
     * @param high 끝 인덱스
     */
    public void sort(TrackingElement[] arr, int low, int high) {
        if (low < high) {
            // 파티션 인덱스 구하기
            int pivotIndex = partition(arr, low, high);

            globalStep++;

            // 파티션을 기준으로 분할하여 재귀적으로 정렬
            sort(arr, low, pivotIndex - 1);
            sort(arr, pivotIndex + 1, high);
        }
    }

    /**
     * 배열의 특정 범위를 파티셔닝합니다.
     * 
     * @param arr 정렬할 배열
     * @param low 시작 인덱스
     * @param high 끝 인덱스
     * @return 피벗의 최종 인덱스
     */
    private int partition(TrackingElement[] arr, int low, int high) {
        // 맨 오른쪽 요소를 피벗으로 선택
        TrackingElement pivot = arr[high];

        // 피벗보다 작은 요소들의 위치를 저장할 인덱스
        int i = low - 1;

        // 파티션 내 단계 카운터
        int partitionStep = 1;

        // 배열을 순회하며 피벗보다 작은 요소는 왼쪽으로 이동
        for (int j = low; j < high; j++) {
            if (arr[j].getValue() <= pivot.getValue()) {
                i++;

                // 교환이 필요한 경우에만 교환
                if (i != j) {
                    // 교환 작업 기록
                    SwapOperation op = new SwapOperation(
                            globalStep, partitionStep, i, j,
                            arr[i], arr[j],
                            "피벗(" + pivot.getValue() + ")보다 작거나 같은 값을 왼쪽으로 이동"
                    );
                    swapLog.add(op);

                    // arr[i]와 arr[j] 교환
                    TrackingElement temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;

                    partitionStep++;
                }
            }
        }

        // 피벗을 올바른 위치로 이동
        int pivotFinalPos = i + 1;

        if (pivotFinalPos != high) {
            // 교환 작업 기록
            SwapOperation op = new SwapOperation(
                    globalStep, partitionStep, pivotFinalPos, high,
                    arr[pivotFinalPos], arr[high],
                    "피벗을 올바른 위치로 이동"
            );
            swapLog.add(op);

            // 피벗과 arr[pivotFinalPos] 교환
            TrackingElement temp = arr[pivotFinalPos];
            arr[pivotFinalPos] = arr[high];
            arr[high] = temp;
        }

        // 피벗의 최종 위치 반환
        return pivotFinalPos;
    }

    /**
     * 교환 작업 기록을 가져옵니다.
     * 
     * @return 교환 작업 목록
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
