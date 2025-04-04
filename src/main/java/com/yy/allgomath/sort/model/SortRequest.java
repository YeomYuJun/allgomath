package com.yy.allgomath.sort.model;

import java.util.List;

public class SortRequest {
    private int arraySize;
    private int minValue;
    private int maxValue;
    private Integer[] customValues; // 사용자 정의 값 (null이면 랜덤 생성)
    private String algorithm; // 정렬 알고리즘 타입

    public SortRequest() {
        // 기본 생성자 (JSON 디시리얼라이제이션용)
    }

    public SortRequest(int arraySize, int minValue, int maxValue) {
        this.arraySize = arraySize;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.customValues = null;
        this.algorithm = "quick"; // 기본값
    }

    public SortRequest(Integer[] customValues) {
        this.customValues = customValues;
        this.arraySize = customValues != null ? customValues.length : 0;
        this.minValue = 0;
        this.maxValue = 0;
        this.algorithm = "quick"; // 기본값
    }

    public SortRequest(Integer[] customValues, String algorithm) {
        this.customValues = customValues;
        this.arraySize = customValues != null ? customValues.length : 0;
        this.minValue = 0;
        this.maxValue = 0;
        this.algorithm = algorithm;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public Integer[] getCustomValues() {
        return customValues;
    }

    public void setCustomValues(Integer[] customValues) {
        this.customValues = customValues;
        if(customValues != null) {
            this.arraySize = customValues.length;
        }
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public boolean hasCustomValues() {
        return customValues != null && customValues.length > 0;
    }
}
