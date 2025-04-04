package com.yy.allgomath.sort.quick;

public class SortRequest {
    private int arraySize;
    private int minValue;
    private int maxValue;
    private Integer[] customValues; // 사용자 정의 값 (null이면 랜덤 생성)

    public SortRequest(int arraySize, int minValue, int maxValue) {
        this.arraySize = arraySize;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.customValues = null;
    }

    public SortRequest(Integer[] customValues) {
        this.customValues = customValues;
        this.arraySize = customValues.length;
        this.minValue = 0;
        this.maxValue = 0;
    }

    public int getArraySize() {
        return arraySize;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public Integer[] getCustomValues() {
        return customValues;
    }

    public boolean hasCustomValues() {
        return customValues != null;
    }
}
