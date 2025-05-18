package com.yy.allgomath.datatype;

public class SwapLog {
    private int step;
    private int index1;
    private int index2;
    private int value1;
    private int value2;

    public SwapLog(int step, int index1, int index2, int value1, int value2) {
        this.step = step;
        this.index1 = index1;
        this.index2 = index2;
        this.value1 = value1;
        this.value2 = value2;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getIndex1() {
        return index1;
    }

    public void setIndex1(int index1) {
        this.index1 = index1;
    }

    public int getIndex2() {
        return index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }
} 