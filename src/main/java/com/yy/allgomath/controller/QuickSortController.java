package com.yy.allgomath.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuickSortController {

    static class TrackingElement {
        int value;      // 정렬될 값
        int sequence;   // 고유 시퀀스 식별자

        public TrackingElement(int value, int sequence) {
            this.value = value;
            this.sequence = sequence;
        }

        @Override
        public String toString() {
            return "[" + value + "," + sequence + "]";
        }
    }

    // 교환 작업 추적을 위한 클래스
    static class SwapOperation {
        int step;           // 전체 단계 번호
        int partitionStep;  // 파티션 내 단계 번호
        int fromIndex;      // 출발 인덱스
        int toIndex;        // 도착 인덱스
        TrackingElement element1;  // 첫 번째 요소
        TrackingElement element2;  // 두 번째 요소
        String reason;      // 교환 이유

        public SwapOperation(int step, int partitionStep, int fromIndex, int toIndex,
                             TrackingElement element1, TrackingElement element2, String reason) {
            this.step = step;
            this.partitionStep = partitionStep;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.element1 = element1;
            this.element2 = element2;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return String.format("Step %d.%d: %s (%d) <-> %s (%d) | %s -> %s | %s",
                    step, partitionStep, element1, fromIndex, element2, toIndex,
                    element1.sequence, element2.sequence, reason);
        }

        // CSV 형식의 문자열 반환
        public String toCSV() {
            return String.format("%d,%d,%d,%d,%d,%d,%d,%d,%s",
                    step, partitionStep, fromIndex, toIndex,
                    element1.value, element1.sequence, element2.value, element2.sequence, reason);
        }
    }

    // 교환 작업 목록
    private static List<SwapOperation> swapLog = new ArrayList<>();
    private static int globalStep = 1;

    public static void main(String[] args) {
        // 10~20 사이의 랜덤 길이를 가진 배열 생성
        Random random = new Random();
        int length = random.nextInt(11) + 10; // 10~20 사이의 랜덤 길이

        // TrackingElement 배열 생성
        TrackingElement[] arr = new TrackingElement[length];

        // 1~100 사이의 랜덤 값과 고유 시퀀스 번호로 배열 채우기
        for (int i = 0; i < length; i++) {
            int value = random.nextInt(100) + 1; // 1~100 사이의 랜덤 값
            arr[i] = new TrackingElement(value, i + 1); // 시퀀스는 1부터 시작
        }

        // 정렬 전 배열 출력
        System.out.println("초기 배열: " + Arrays.toString(arr));

        // 퀵 정렬 수행
        quickSort(arr, 0, arr.length - 1);

        // 정렬 후 배열 출력
        System.out.println("\n최종 정렬된 배열: " + Arrays.toString(arr));

        // 교환 작업 로그 출력
        System.out.println("\n-------- 교환 작업 로그 (총 " + swapLog.size() + "회) --------");
        for (SwapOperation op : swapLog) {
            System.out.println(op);
        }

        // CSV 형식의 헤더와 데이터 출력 (시각화를 위한 데이터)
        System.out.println("\n-------- CSV 형식 데이터 --------");
        System.out.println("step,partitionStep,fromIndex,toIndex,element1Value,element1Sequence,element2Value,element2Sequence,reason");
        for (SwapOperation op : swapLog) {
            System.out.println(op.toCSV());
        }
    }

    // 퀵 정렬 알고리즘 구현
    public static void quickSort(TrackingElement[] arr, int low, int high) {
        if (low < high) {
            System.out.println("\n[단계 " + globalStep + "] 퀵 정렬 수행: " + low + "부터 " + high + "까지");

            // 현재 범위의 배열 출력
            System.out.print("현재 범위: ");
            for (int i = low; i <= high; i++) {
                System.out.print(arr[i] + " ");
            }
            System.out.println();

            // 파티션 인덱스 구하기
            int pivotIndex = partition(arr, low, high);

            globalStep++;

            // 파티션을 기준으로 분할하여 재귀적으로 정렬
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    // 파티션 함수 구현
    public static int partition(TrackingElement[] arr, int low, int high) {
        // 맨 오른쪽 요소를 피벗으로 선택
        TrackingElement pivot = arr[high];
        System.out.println("피벗 선택: " + pivot + " (인덱스 " + high + ")");

        // 피벗보다 작은 요소들의 위치를 저장할 인덱스
        int i = low - 1;

        // 파티션 내 단계 카운터
        int partitionStep = 1;

        // 배열을 순회하며 피벗보다 작은 요소는 왼쪽으로 이동
        for (int j = low; j < high; j++) {
            System.out.println("검사 중: arr[" + j + "] = " + arr[j]);

            if (arr[j].value <= pivot.value) {
                i++;

                // 교환이 필요한 경우에만 교환
                if (i != j) {
                    System.out.println("  교환: arr[" + i + "]=" + arr[i] + " <-> arr[" + j + "]=" + arr[j]);

                    // 교환 작업 기록
                    SwapOperation op = new SwapOperation(
                            globalStep, partitionStep, i, j,
                            arr[i], arr[j],
                            "피벗(" + pivot.value + ")보다 작거나 같은 값을 왼쪽으로 이동"
                    );
                    swapLog.add(op);

                    // arr[i]와 arr[j] 교환
                    TrackingElement temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;

                    partitionStep++;
                } else {
                    System.out.println("  같은 위치이므로 교환 불필요 (i=j=" + i + ")");
                }
            } else {
                System.out.println("  " + arr[j].value + " > " + pivot.value + " 이므로 교환하지 않음");
            }
        }

        // 피벗을 올바른 위치로 이동
        int pivotFinalPos = i + 1;

        if (pivotFinalPos != high) {
            System.out.println("피벗 이동: arr[" + pivotFinalPos + "]=" + arr[pivotFinalPos] +
                    " <-> 피벗 arr[" + high + "]=" + arr[high]);

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
        } else {
            System.out.println("피벗이 이미 올바른 위치에 있음 (인덱스 " + high + ")");
        }

        System.out.println("파티션 완료: 피벗(" + pivot + ")의 최종 위치는 인덱스 " + pivotFinalPos);

        // 파티션 결과 출력
        System.out.print("현재 배열 상태: ");
        for (TrackingElement e : arr) {
            System.out.print(e + " ");
        }
        System.out.println();

        // 피벗의 최종 위치 반환
        return pivotFinalPos;
    }

}
