package com.yy.allgomath.plotter;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.plotter.dto.DescentParams;
import com.yy.allgomath.plotter.dto.DescentResult;
import com.yy.allgomath.plotter.dto.GradPoint;
import com.yy.allgomath.plotter.dto.SurfaceParams;
import com.yy.allgomath.plotter.dto.SurfaceResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Surface grid sampling + robust gradient descent (gradient clipping, backtracking line search). */
@Service
public class PlotterService {

    private static final double GRAD_NORM_MAX = 1e3;
    private static final double STEP_NORM_MAX = 1.0;
    private static final double COORD_ABS_MAX = 1e3;
    private static final int LINESEARCH_MAX_TRIES = 5;

    public SurfaceResult surface(SurfaceParams params) {
        if (params.range() < 2 || params.range() > 12) {
            throw new InvalidParameterException("range는 2~12 사이여야 합니다.");
        }
        SurfaceFunction fn = SurfaceFunction.of(params.fn());
        int n = params.resolution();
        double range = params.range();
        double step = (range * 2) / n;
        double[][] z = new double[n + 1][n + 1];
        double zMin = Double.POSITIVE_INFINITY, zMax = Double.NEGATIVE_INFINITY;
        for (int i = 0; i <= n; i++) {
            double x = -range + i * step;
            for (int j = 0; j <= n; j++) {
                double y = -range + j * step;
                double v = fn.z(x, y);
                z[i][j] = v;
                if (v < zMin) zMin = v;
                if (v > zMax) zMax = v;
            }
        }
        double[] c = fn.critical();
        GradPoint critical = new GradPoint(0, c[0], c[1], fn.z(c[0], c[1]), 0, 0);
        return new SurfaceResult(z, zMin, zMax, critical);
    }

    public DescentResult descend(DescentParams params) {
        if (params.learningRate() <= 0 || params.learningRate() > 1) {
            throw new InvalidParameterException("learningRate는 0 초과 1 이하여야 합니다.");
        }
        SurfaceFunction fn = SurfaceFunction.of(params.fn());
        int maxIter = params.maxIterations();
        double lr = params.learningRate();
        double x = params.startX(), y = params.startY();
        List<GradPoint> path = new ArrayList<>();
        boolean converged = false;
        int last = 0;

        for (int i = 0; i <= maxIter; i++) {
            last = i;
            double cz = fn.z(x, y);
            double[] g = fn.grad(x, y);
            double gx = g[0], gy = g[1];
            if (!Double.isFinite(cz) || !Double.isFinite(gx) || !Double.isFinite(gy)) break;
            path.add(new GradPoint(i, x, y, cz, gx, gy));
            if (Math.hypot(gx, gy) < 1e-6) {
                converged = true;
                break;
            }
            if (i == maxIter) break;

            double gnorm = Math.hypot(gx, gy);
            if (gnorm > GRAD_NORM_MAX) {
                double s = GRAD_NORM_MAX / gnorm;
                gx *= s;
                gy *= s;
            }
            double stepX = lr * gx, stepY = lr * gy;
            double stepNorm = Math.hypot(stepX, stepY);
            if (stepNorm > STEP_NORM_MAX) {
                double s = STEP_NORM_MAX / stepNorm;
                stepX *= s;
                stepY *= s;
            }

            double t = 1.0;
            double nx = x - t * stepX, ny = y - t * stepY, nz = fn.z(nx, ny);
            int tries = 0;
            while (tries < LINESEARCH_MAX_TRIES && (!Double.isFinite(nz) || nz > cz)) {
                t *= 0.5;
                nx = x - t * stepX;
                ny = y - t * stepY;
                nz = fn.z(nx, ny);
                tries++;
            }
            if (!Double.isFinite(nz) || nz > cz) break;
            x = nx;
            y = ny;
            if (Math.abs(x) > COORD_ABS_MAX || Math.abs(y) > COORD_ABS_MAX) break;
        }
        return new DescentResult(path, converged, last);
    }
}
