package com.yy.allgomath.sort.quick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuickSortService {
    private List<SwapOperation> swapLog;
    private int globalStep;

    public QuickSortService() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    // 퀵 정렬 수행 메서드 (공개 API)
    public SortResult performQuickSort(SortRequest request) {
        // 로그 초기화
        swapLog = new ArrayList<>();
        globalStep = 1;

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
        quickSort(arr, 0, arr.length - 1);

        // 결과 반환
        return new SortResult(arr, swapLog, globalStep - 1);
    }

    // 퀵 정렬 알고리즘 구현
    private void quickSort(TrackingElement[] arr, int low, int high) {
        if (low < high) {
            // 파티션 인덱스 구하기
            int pivotIndex = partition(arr, low, high);

            globalStep++;

            // 파티션을 기준으로 분할하여 재귀적으로 정렬
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    // 파티션 함수 구현
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
}
