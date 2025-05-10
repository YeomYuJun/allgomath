package com.yy.allgomath.datatype;

// 시간 도메인 데이터
public class TimeDomainSignal {
    private double[] realValues;
    private double samplingRate;

    public TimeDomainSignal() {}

    public TimeDomainSignal(double[] realValues, double samplingRate) {
        this.realValues = realValues;
        this.samplingRate = samplingRate;
    }

    // Getter와 Setter 메서드
    public double[] getRealValues() {
        return realValues;
    }

    public void setRealValues(double[] realValues) {
        this.realValues = realValues;
    }

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }
}
