package com.yy.allgomath.sort.quick;

public class TrackingElement {
    private int value;      // 정렬될 값
    private int sequence;   // 고유 시퀀스 식별자

    public TrackingElement(int value, int sequence) {
        this.value = value;
        this.sequence = sequence;
    }

    public int getValue() {
        return value;
    }

    public int getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "[" + value + "," + sequence + "]";
    }
}
