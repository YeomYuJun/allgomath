package com.yy.allgomath.simulation;

/** 초기 상태+파라미터(P)에서 다음 N step의 상태(S)를 계산하는 stateless 배치 시뮬레이터. */
public interface BatchSimulator<P, S> {
    SimulationResponse<S> simulate(P params, int steps);
}
