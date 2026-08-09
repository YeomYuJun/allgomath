package com.yy.allgomath.simulation;

import java.util.ArrayList;
import java.util.List;

/** 배치 simulate 공통 응답: 다음 N step 상태 목록 + step별 파생 스칼라. */
public record SimulationResponse<S>(List<S> steps, double[] series) {

    /**
     * 프레임 수를 maxFrames 이하로 균등 다운샘플한다(series와 lockstep). 마지막 프레임은 항상 포함해
     * 최종 상태를 보존한다. FE는 프레임을 배열 순서로 소비하므로 안전하다.
     */
    public static <S> SimulationResponse<S> capped(List<S> steps, double[] series, int maxFrames) {
        int n = steps.size();
        if (n <= maxFrames) {
            return new SimulationResponse<>(steps, series);
        }
        int stride = (int) Math.ceil((double) n / maxFrames);
        List<S> outSteps = new ArrayList<>();
        List<Double> outSeries = new ArrayList<>();
        for (int i = 0; i < n; i += stride) {
            outSteps.add(steps.get(i));
            outSeries.add(series[i]);
        }
        if (outSteps.get(outSteps.size() - 1) != steps.get(n - 1)) {
            outSteps.add(steps.get(n - 1));
            outSeries.add(series[n - 1]);
        }
        double[] outSeriesArr = new double[outSeries.size()];
        for (int i = 0; i < outSeriesArr.length; i++) {
            outSeriesArr[i] = outSeries.get(i);
        }
        return new SimulationResponse<>(outSteps, outSeriesArr);
    }
}
