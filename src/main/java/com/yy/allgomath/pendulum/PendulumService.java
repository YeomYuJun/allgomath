package com.yy.allgomath.pendulum;

import com.yy.allgomath.pendulum.dto.Bob;
import com.yy.allgomath.pendulum.dto.PendulumParams;
import com.yy.allgomath.pendulum.dto.PendulumState;
import com.yy.allgomath.simulation.BatchSimulator;
import com.yy.allgomath.simulation.SimulationResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 이중진자 RK4 적분(m1=m2=1, L1=1, L2=armRatio). 원본/트윈 두 진자를 독립 적분. */
@Service
public class PendulumService implements BatchSimulator<PendulumParams, PendulumState> {

    private static final double DT = 0.1;
    private static final int SUBSTEPS = 4;

    @Override
    public SimulationResponse<PendulumState> simulate(PendulumParams params, int steps) {
        double[] a = toArray(params.state().a());
        double[] b = toArray(params.state().b());
        List<PendulumState> out = new ArrayList<>(steps);
        double[] series = new double[steps];
        for (int i = 0; i < steps; i++) {
            advance(a, params);
            advance(b, params);
            out.add(new PendulumState(toBob(a), toBob(b)));
            series[i] = divergence(a, b, params.armRatio());
        }
        return new SimulationResponse<>(out, series);
    }

    private void advance(double[] y, PendulumParams p) {
        double h = DT / SUBSTEPS;
        for (int s = 0; s < SUBSTEPS; s++) {
            rk4(y, p, h);
            y[2] *= (1 - p.damping());
            y[3] *= (1 - p.damping());
        }
    }

    private void rk4(double[] y, PendulumParams p, double h) {
        double[] k1 = deriv(y, p);
        double[] k2 = deriv(add(y, k1, h / 2), p);
        double[] k3 = deriv(add(y, k2, h / 2), p);
        double[] k4 = deriv(add(y, k3, h), p);
        for (int i = 0; i < 4; i++) {
            y[i] += h / 6 * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
        }
    }

    private static double[] add(double[] y, double[] k, double h) {
        return new double[]{y[0] + k[0] * h, y[1] + k[1] * h, y[2] + k[2] * h, y[3] + k[3] * h};
    }

    /** y={t1,t2,w1,w2} -> {w1,w2,a1,a2}. Lagrangian EOM (m1=m2=1, L1=1, L2=r). */
    private static double[] deriv(double[] y, PendulumParams p) {
        double t1 = y[0], t2 = y[1], w1 = y[2], w2 = y[3];
        double r = p.armRatio(), g = p.gravity();
        double d = t1 - t2, cd = Math.cos(d), sd = Math.sin(d);
        double r2 = r * r;
        double det = r2 * (2 - cd * cd);
        double f0 = -r * w2 * w2 * sd - 2 * g * Math.sin(t1);
        double f1 = r * w1 * w1 * sd - g * r * Math.sin(t2);
        double a1 = (f0 * r2 - f1 * r * cd) / det;
        double a2 = (2 * f1 - r * cd * f0) / det;
        return new double[]{w1, w2, a1, a2};
    }

    private static double divergence(double[] a, double[] b, double r) {
        double[] ta = tip(a, r), tb = tip(b, r);
        return Math.hypot(ta[0] - tb[0], ta[1] - tb[1]);
    }

    private static double[] tip(double[] y, double r) {
        double x1 = Math.sin(y[0]), y1 = -Math.cos(y[0]);
        return new double[]{x1 + r * Math.sin(y[1]), y1 - r * Math.cos(y[1])};
    }

    private static double[] toArray(Bob b) {
        return new double[]{b.t1(), b.t2(), b.w1(), b.w2()};
    }

    private static Bob toBob(double[] y) {
        return new Bob(y[0], y[1], y[2], y[3]);
    }
}
