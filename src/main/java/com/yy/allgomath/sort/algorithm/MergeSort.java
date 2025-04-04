package com.yy.allgomath.sort.algorithm;

import com.yy.allgomath.sort.model.SwapOperation;
import com.yy.allgomath.sort.model.TrackingElement;

import java.util.ArrayList;
import java.util.List;

/**
 * 합병 정렬 알고리즘 구현
 */
public class MergeSort {
    private List<SwapOperation> mergeLog;
    private int globalStep;

    public MergeSort() {
        this.mergeLog = new ArrayList<>();
        this.globalStep = 1;
    }

    public void reset() {
        this.mergeLog = new ArrayList<>();
        this.globalStep = 1;
    }

    /**
     * 합병 정렬 알고리즘을 시작합니다.
     * 
     * @param arr 정렬할 배열
     */
    public void sort(TrackingElement[] arr) {
        TrackingElement[] temp = new TrackingElement[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }

    /**
     * 합병 정렬의 재귀 부분
     * 
     * @param arr 정렬할 배열
     * @param temp 임시 배열
     * @param left 왼쪽 인덱스
     * @param right 오른쪽 인덱스
     */
    private void mergeSort(TrackingElement[] arr, TrackingElement[] temp, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            // 왼쪽 절반 정렬
            mergeSort(arr, temp, left, mid);
            
            // 오른쪽 절반 정렬
            mergeSort(arr, temp, mid + 1, right);
            
            // 정렬된 두 부분 합병
            merge(arr, temp, left, mid, right);
            
            // 다음 단계로 진행
            globalStep++;
        }
    }

    /**
     * 두 정렬된 부분 배열을 합병합니다.
     * 
     * @param arr 원본 배열
     * @param temp 임시 배열
     * @param left 왼쪽 시작 인덱스
     * @param mid 중간 인덱스
     * @param right 오른쪽 끝 인덱스
     */
    private void merge(TrackingElement[] arr, TrackingElement[] temp, int left, int mid, int right) {
        // 임시 배열에 원본 배열 복사
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        
        int i = left;      // 왼쪽 부분 배열의 시작 인덱스
        int j = mid + 1;   // 오른쪽 부분 배열의 시작 인덱스
        int k = left;      // 결과 배열의 현재 인덱스
        int mergeStep = 1; // 현재 합병 단계 내의 단계 번호
        
        // 두 부분 배열을 비교하며 작은 값을 선택하여 원본 배열에 삽입
        while (i <= mid && j <= right) {
            if (temp[i].getValue() <= temp[j].getValue()) {
                if (k != i) { // 원래 위치가 아닌 경우만 로그 기록
                    // 합병 로그 추가
                    SwapOperation op = new SwapOperation(
                            globalStep, mergeStep, i, k,
                            temp[i], arr[k],
                            String.format("왼쪽 부분 배열 요소(%d)가 오른쪽 부분 배열 요소(%d)보다 작거나 같음", 
                                        temp[i].getValue(), j <= right ? temp[j].getValue() : -1)
                    );
                    mergeLog.add(op);
                    mergeStep++;
                }
                arr[k] = temp[i];
                i++;
            } else {
                if (k != j) { // 원래 위치가 아닌 경우만 로그 기록
                    // 합병 로그 추가
                    SwapOperation op = new SwapOperation(
                            globalStep, mergeStep, j, k,
                            temp[j], arr[k],
                            String.format("오른쪽 부분 배열 요소(%d)가 왼쪽 부분 배열 요소(%d)보다 작음", 
                                        temp[j].getValue(), temp[i].getValue())
                    );
                    mergeLog.add(op);
                    mergeStep++;
                }
                arr[k] = temp[j];
                j++;
            }
            k++;
        }
        
        // 왼쪽 부분 배열에 남은 요소들 처리
        while (i <= mid) {
            if (k != i) { // 원래 위치가 아닌 경우만 로그 기록
                // 합병 로그 추가
                SwapOperation op = new SwapOperation(
                        globalStep, mergeStep, i, k,
                        temp[i], arr[k],
                        "오른쪽 부분 배열이 모두 사용됨, 왼쪽 부분 배열의 남은 요소 복사"
                );
                mergeLog.add(op);
                mergeStep++;
            }
            arr[k] = temp[i];
            i++;
            k++;
        }
        
        // 오른쪽 부분 배열에 남은 요소들 처리
        while (j <= right) {
            if (k != j) { // 원래 위치가 아닌 경우만 로그 기록
                // 합병 로그 추가
                SwapOperation op = new SwapOperation(
                        globalStep, mergeStep, j, k,
                        temp[j], arr[k],
                        "왼쪽 부분 배열이 모두 사용됨, 오른쪽 부분 배열의 남은 요소 복사"
                );
                mergeLog.add(op);
                mergeStep++;
            }
            arr[k] = temp[j];
            j++;
            k++;
        }
    }

    /**
     * 합병 작업 로그를 가져옵니다.
     * 
     * @return 합병 작업 로그
     */
    public List<SwapOperation> getMergeLog() {
        return mergeLog;
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
