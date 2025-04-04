package com.yy.allgomath.sort.model;

import java.util.List;

public class SortResult {
    private TrackingElement[] sortedArray;
    private List<SwapOperation> swapOperations;
    private int totalSteps;
    private String algorithm;

    public SortResult(TrackingElement[] sortedArray, List<SwapOperation> swapOperations, int totalSteps) {
        this.sortedArray = sortedArray;
        this.swapOperations = swapOperations;
        this.totalSteps = totalSteps;
        this.algorithm = "quick"; // 기본값
    }

    public SortResult(TrackingElement[] sortedArray, List<SwapOperation> swapOperations, int totalSteps, String algorithm) {
        this.sortedArray = sortedArray;
        this.swapOperations = swapOperations;
        this.totalSteps = totalSteps;
        this.algorithm = algorithm;
    }

    public TrackingElement[] getSortedArray() {
        return sortedArray;
    }

    public List<SwapOperation> getSwapOperations() {
        return swapOperations;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    // CSV 형식의 전체 교환 작업 로그 생성
    public String getSwapOperationsCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("step,partitionStep,fromIndex,toIndex,element1Value,element1Sequence,element2Value,element2Sequence,reason\n");

        for (SwapOperation op : swapOperations) {
            sb.append(op.toText()).append("\n");
        }

        return sb.toString();
    }
}
