package com.yy.allgomath.util.algorithm;

import com.yy.allgomath.datatype.TrackingElement;
import com.yy.allgomath.datatype.SwapLog;

import java.util.ArrayList;
import java.util.List;

public class MergeSort {
    private List<SwapLog> swapLog;
    private int globalStep;

    public MergeSort() {
        this.swapLog = new ArrayList<>();
        this.globalStep = 1;
    }

    public void sort(TrackingElement[] arr, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;
            sort(arr, left, middle);
            sort(arr, middle + 1, right);
            merge(arr, left, middle, right);
        }
    }

    private void merge(TrackingElement[] arr, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        TrackingElement[] leftArr = new TrackingElement[n1];
        TrackingElement[] rightArr = new TrackingElement[n2];

        for (int i = 0; i < n1; i++) {
            leftArr[i] = arr[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArr[j] = arr[middle + 1 + j];
        }

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (leftArr[i].getValue() <= rightArr[j].getValue()) {
                swap(arr, k, leftArr[i], i);
                i++;
            } else {
                swap(arr, k, rightArr[j], j);
                j++;
            }
            k++;
        }

        while (i < n1) {
            swap(arr, k, leftArr[i], i);
            i++;
            k++;
        }

        while (j < n2) {
            swap(arr, k, rightArr[j], j);
            j++;
            k++;
        }
    }

    private void swap(TrackingElement[] arr, int destIndex, TrackingElement element, int sourceIndex) {
        arr[destIndex] = element;
        swapLog.add(new SwapLog(globalStep++, sourceIndex, destIndex, element.getValue(), element.getValue()));
    }

    public List<SwapLog> getSwapLog() {
        return swapLog;
    }

    public int getGlobalStep() {
        return globalStep;
    }
} 