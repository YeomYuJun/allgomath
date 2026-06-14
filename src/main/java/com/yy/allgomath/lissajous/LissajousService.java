package com.yy.allgomath.lissajous;

import com.yy.allgomath.lissajous.dto.LissajousParams;
import com.yy.allgomath.lissajous.dto.Point;
import com.yy.allgomath.simulation.BatchSimulator;
import com.yy.allgomath.simulation.SimulationResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 리사주 곡선 x=sin(a·u+δ), y=sin(b·u). 위상 u를 상태로 다음 N점 반환(resumable). */
@Service
public class LissajousService implements BatchSimulator<LissajousParams, Point> {

    private static final double DU = 2 * Math.PI / 300;

    @Override
    public SimulationResponse<Point> simulate(LissajousParams params, int steps) {
        double u = params.phase();
        List<Point> out = new ArrayList<>(steps);
        double[] series = new double[steps];
        for (int i = 0; i < steps; i++) {
            u += DU;
            out.add(new Point(Math.sin(params.a() * u + params.delta()), Math.sin(params.b() * u)));
            series[i] = u;
        }
        return new SimulationResponse<>(out, series);
    }
}
