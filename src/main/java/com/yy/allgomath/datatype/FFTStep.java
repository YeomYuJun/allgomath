package com.yy.allgomath.datatype;

// FFT 변환 단계 데이터 모델
public class FFTStep {
    private int level;
    private Complex[] data;
    private String description;

    public FFTStep() {}

    public FFTStep(int level, Complex[] data, String description) {
        this.level = level;
        this.data = data;
        this.description = description;
    }

    // Getter와 Setter 메서드
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Complex[] getData() {
        return data;
    }

    public void setData(Complex[] data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}