package com.yy.allgomath.datatype;

import java.util.List;

public class SortResult {
    private TrackingElement[] sortedArray;
    private List<SwapLog> swapLog;
    private int totalSteps;
    private String algorithm;

    public SortResult(TrackingElement[] sortedArray, List<SwapLog> swapLog, int totalSteps, String algorithm) {
        this.sortedArray = sortedArray;
        this.swapLog = swapLog;
        this.totalSteps = totalSteps;
        this.algorithm = algorithm;
    }

    public TrackingElement[] getSortedArray() {
        return sortedArray;
    }

    public void setSortedArray(TrackingElement[] sortedArray) {
        this.sortedArray = sortedArray;
    }

    public List<SwapLog> getSwapLog() {
        return swapLog;
    }

    public void setSwapLog(List<SwapLog> swapLog) {
        this.swapLog = swapLog;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
} 