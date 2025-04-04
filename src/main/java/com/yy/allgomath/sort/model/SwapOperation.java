package com.yy.allgomath.sort.model;

public class SwapOperation {
    private int step;           // 전체 단계 번호
    private int partitionStep;  // 파티션 내 단계 번호
    private int fromIndex;      // 출발 인덱스
    private int toIndex;        // 도착 인덱스
    private TrackingElement element1;  // 첫 번째 요소
    private TrackingElement element2;  // 두 번째 요소
    private String reason;      // 교환 이유

    public SwapOperation(int step, int partitionStep, int fromIndex, int toIndex,
                         TrackingElement element1, TrackingElement element2, String reason) {
        this.step = step;
        this.partitionStep = partitionStep;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.element1 = element1;
        this.element2 = element2;
        this.reason = reason;
    }

    // Getters
    public int getStep() { return step; }
    public int getPartitionStep() { return partitionStep; }
    public int getFromIndex() { return fromIndex; }
    public int getToIndex() { return toIndex; }
    public TrackingElement getElement1() { return element1; }
    public TrackingElement getElement2() { return element2; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return String.format("Step %d.%d: %s (%d) <-> %s (%d) | %s -> %s | %s",
                step, partitionStep, element1, fromIndex, element2, toIndex,
                element1.getSequence(), element2.getSequence(), reason);
    }

    // CSV 형식의 문자열 반환
    public String toText() {
        return String.format("%d,%d,%d,%d,%d,%d,%d,%d,%s",
                step, partitionStep, fromIndex, toIndex,
                element1.getValue(), element1.getSequence(),
                element2.getValue(), element2.getSequence(), reason);
    }
}
