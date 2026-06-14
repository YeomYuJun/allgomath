package com.yy.allgomath.simulation;

import java.util.List;

/** 배치 simulate 공통 응답: 다음 N step 상태 목록 + step별 파생 스칼라. */
public record SimulationResponse<S>(List<S> steps, double[] series) {
}
