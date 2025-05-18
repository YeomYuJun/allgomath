package com.yy.allgomath.util.algorithm;

import com.yy.allgomath.datatype.TrackingElement;
import com.yy.allgomath.datatype.SwapLog;

import java.util.ArrayList;
import java.util.List;

public class HeapSort {
    private List<SwapLog> swapLog;
    private int globalStep;

    public HeapSort() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    public void sort(TrackingElement[] arr) {
        int n = arr.length;

        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);

        // One by one extract an element from heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            swap(arr, 0, i);

            // call max heapify on the reduced heap
            heapify(arr, i, 0);
        }
    }

    private void heapify(TrackingElement[] arr, int n, int i) {
        int largest = i; // Initialize largest as root
        int l = 2 * i + 1; // left = 2*i + 1
        int r = 2 * i + 2; // right = 2*i + 2

        // If left child is larger than root
        if (l < n && arr[l].getValue() > arr[largest].getValue())
            largest = l;

        // If right child is larger than largest so far
        if (r < n && arr[r].getValue() > arr[largest].getValue())
            largest = r;

        // If largest is not root
        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
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