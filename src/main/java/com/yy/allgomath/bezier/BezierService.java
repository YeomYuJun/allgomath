package com.yy.allgomath.bezier;

import com.yy.allgomath.bezier.dto.BezierParams;
import com.yy.allgomath.bezier.dto.BezierResult;
import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

/** de Casteljau 곡선 샘플링. 정규화 [0,1] 좌표, one-shot. */
@Service("bezierDomainService")
public class BezierService implements Computer<BezierParams, BezierResult> {

    private static final int MAX_POINTS = 8;

    @Override
    public BezierResult compute(BezierParams params) {
        double[][] cp = params.controlPoints();
        validate(cp);
        int samples = params.samples();
        double[][] curve = new double[samples + 1][];
        for (int i = 0; i <= samples; i++) {
            curve[i] = pointAt(cp, (double) i / samples);
        }
        return new BezierResult(curve, cp.length - 1);
    }

    private static double[] pointAt(double[][] cp, double t) {
        double[] x = new double[cp.length];
        double[] y = new double[cp.length];
        for (int i = 0; i < cp.length; i++) {
            x[i] = cp[i][0];
            y[i] = cp[i][1];
        }
        for (int level = cp.length - 1; level > 0; level--) {
            for (int i = 0; i < level; i++) {
                x[i] = x[i] + (x[i + 1] - x[i]) * t;
                y[i] = y[i] + (y[i + 1] - y[i]) * t;
            }
        }
        return new double[]{x[0], y[0]};
    }

    private void validate(double[][] cp) {
        if (cp == null || cp.length < 2 || cp.length > MAX_POINTS) {
            throw new InvalidParameterException("제어점은 2~" + MAX_POINTS + "개여야 합니다.");
        }
        for (double[] p : cp) {
            if (p == null || p.length != 2) {
                throw new InvalidParameterException("각 제어점은 [x, y] 형식이어야 합니다.");
            }
        }
    }
}
