package com.yy.allgomath.datatype;

public class FractalResult {
    private int width;
    private int height;
    private int[][] iterationCounts; // 또는 byte[] 이미지 데이터? 흠..

    public FractalResult(int width, int height, int[][] iterationCounts) {
        this.width = width;
        this.height = height;
        this.iterationCounts = iterationCounts;
    }
}