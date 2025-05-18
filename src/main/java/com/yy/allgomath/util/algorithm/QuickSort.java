package com.yy.allgomath.util.algorithm;

import com.yy.allgomath.datatype.TrackingElement;
import com.yy.allgomath.datatype.SwapLog;

import java.util.ArrayList;
import java.util.List;

public class QuickSort {
    private List<SwapLog> swapLog;
    private int globalStep;

    public QuickSort() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    public void sort(TrackingElement[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    private int partition(TrackingElement[] arr, int low, int high) {
        TrackingElement pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (arr[j].getValue() < pivot.getValue()) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(TrackingElement[] arr, int i, int j) {
        TrackingElement temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;

        // 교환 로그 기록
        swapLog.add(new SwapLog(globalStep++, i, j, arr[i].getValue(), arr[j].getValue()));
    }

    public List<SwapLog> getSwapLog() {
        return swapLog;
    }

    public int getGlobalStep() {
        return globalStep;
    }
} 