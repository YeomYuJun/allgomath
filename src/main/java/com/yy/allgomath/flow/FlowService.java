package com.yy.allgomath.flow;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.flow.dto.FlowParams;
import com.yy.allgomath.simulation.BatchSimulator;
import com.yy.allgomath.simulation.SimulationResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Perlin(값-노이즈) 벡터장 입자 advection. 순수·결정적, resumable. */
@Service
public class FlowService implements BatchSimulator<FlowParams, double[][]> {

    private static final int MAX_PARTICLES = 1500;
    private static final double K = 0.03;
    private static final double STEP = 0.6;
    private static final double TIME_STEP = 0.01;

    @Override
    public SimulationResponse<double[][]> simulate(FlowParams params, int steps) {
        validate(params.particles());
        double[][] cur = deepCopy(params.particles());
        double time = params.time();
        List<double[][]> out = new ArrayList<>(steps);
        double[] series = new double[steps];
        for (int i = 0; i < steps; i++) {
            for (double[] p : cur) {
                double a = noise(p[0] * K * params.scale(), p[1] * K * params.scale()) * 2 * Math.PI + time;
                p[0] += Math.cos(a) * STEP;
                p[1] += Math.sin(a) * STEP;
            }
            time += TIME_STEP;
            out.add(deepCopy(cur));
            series[i] = time;
        }
        return new SimulationResponse<>(out, series);
    }

    /** 값-노이즈 [0,1): 격자 해시를 smoothstep 이중보간. */
    static double noise(double x, double y) {
        int xi = (int) Math.floor(x), yi = (int) Math.floor(y);
        double xf = x - xi, yf = y - yi;
        double u = xf * xf * (3 - 2 * xf), v = yf * yf * (3 - 2 * yf);
        return lerp(lerp(hash(xi, yi), hash(xi + 1, yi), u),
                lerp(hash(xi, yi + 1), hash(xi + 1, yi + 1), u), v);
    }

    private static double hash(int ix, int iy) {
        int h = ix * 374761393 + iy * 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return (Integer.toUnsignedLong(h) % 100000) / 100000.0;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double[][] deepCopy(double[][] src) {
        double[][] c = new double[src.length][];
        for (int i = 0; i < src.length; i++) c[i] = src[i].clone();
        return c;
    }

    private void validate(double[][] particles) {
        if (particles == null || particles.length == 0 || particles.length > MAX_PARTICLES) {
            throw new InvalidParameterException("입자 수는 1~" + MAX_PARTICLES + " 사이여야 합니다.");
        }
        for (double[] p : particles) {
            if (p == null || p.length != 2) {
                throw new InvalidParameterException("각 입자는 [x, y] 형식이어야 합니다.");
            }
        }
    }
}
