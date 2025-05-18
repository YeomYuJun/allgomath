package com.yy.allgomath.datatype;

public class SortRequest {
    private String algorithm;
    private Integer[] customValues;
    private int arraySize;
    private int minValue;
    private int maxValue;

    public SortRequest() {
    }

    public SortRequest(Integer[] customValues) {
        this.customValues = customValues;
    }

    public SortRequest(int arraySize, int minValue, int maxValue) {
        this.arraySize = arraySize;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer[] getCustomValues() {
        return customValues;
    }

    public void setCustomValues(Integer[] customValues) {
        this.customValues = customValues;
    }

    public boolean hasCustomValues() {
        return customValues != null && customValues.length > 0;
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
} 