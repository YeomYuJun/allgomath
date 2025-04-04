package com.yy.allgomath.sort.quick;

import java.util.List;

public class SortResult {
    private TrackingElement[] sortedArray;
    private List<SwapOperation> swapOperations;
    private int totalSteps;

    public SortResult(TrackingElement[] sortedArray, List<SwapOperation> swapOperations, int totalSteps) {
        this.sortedArray = sortedArray;
        this.swapOperations = swapOperations;
        this.totalSteps = totalSteps;
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
