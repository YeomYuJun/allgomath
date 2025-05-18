package com.yy.allgomath.datatype;

public class TrackingElement {
    private int value;
    private int originalIndex;

    public TrackingElement(int value, int originalIndex) {
        this.value = value;
        this.originalIndex = originalIndex;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }
} 