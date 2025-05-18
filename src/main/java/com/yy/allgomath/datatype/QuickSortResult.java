package com.yy.allgomath.datatype;

import java.util.List;

public class QuickSortResult {
    private TrackingElement[] sortedArray;
    private List<SwapLog> swapLog;
    private int totalSteps;

    public QuickSortResult(TrackingElement[] sortedArray, List<SwapLog> swapLog, int totalSteps) {
        this.sortedArray = sortedArray;
        this.swapLog = swapLog;
        this.totalSteps = totalSteps;
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

    // CSV 형식의 전체 교환 작업 로그 생성
    public String getSwapLogCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("step,partitionStep,fromIndex,toIndex,element1Value,element1Sequence,element2Value,element2Sequence,reason\n");

        for (SwapLog op : swapLog) {
            sb.append(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%s\n",
                    op.getStep(), op.getIndex1(), op.getIndex2(),
                    op.getValue1(), op.getValue2()));
        }

        return sb.toString();
    }
} 