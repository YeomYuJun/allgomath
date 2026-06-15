package com.yy.allgomath.plotter;

import com.yy.allgomath.common.exception.InvalidParameterException;

/** 3D plotter surface catalog. Each constant provides analytic z, grad and a representative critical point. */
public enum SurfaceFunction {
    BOWL("Paraboloid", "x²+y²", 0, 0) {
        @Override public double z(double x, double y) { return x * x + y * y; }
        @Override public double[] grad(double x, double y) { return new double[]{2 * x, 2 * y}; }
    },
    SADDLE("Saddle", "x²−y²", 0, 0) {
        @Override public double z(double x, double y) { return x * x - y * y; }
        @Override public double[] grad(double x, double y) { return new double[]{2 * x, -2 * y}; }
    },
    MONKEY("Monkey saddle", "x³−3xy²", 0, 0) {
        @Override public double z(double x, double y) { return x * x * x - 3 * x * y * y; }
        @Override public double[] grad(double x, double y) { return new double[]{3 * x * x - 3 * y * y, -6 * x * y}; }
    },
    GAUSSIAN("Gaussian well", "−e^−(x²+y²)", 0, 0) {
        @Override public double z(double x, double y) { return -Math.exp(-(x * x + y * y)); }
        @Override public double[] grad(double x, double y) {
            double e = Math.exp(-(x * x + y * y));
            return new double[]{2 * x * e, 2 * y * e};
        }
    },
    RIPPLE("Ripple", "0.1(x²+y²)+0.2·sinx·siny", 0, 0) {
        @Override public double z(double x, double y) { return 0.1 * (x * x + y * y) + 0.2 * Math.sin(x) * Math.sin(y); }
        @Override public double[] grad(double x, double y) {
            return new double[]{0.2 * x + 0.2 * Math.cos(x) * Math.sin(y), 0.2 * y + 0.2 * Math.sin(x) * Math.cos(y)};
        }
    },
    ROSENBROCK("Rosenbrock", "(1−x)²+100(y−x²)²", 1, 1) {
        @Override public double z(double x, double y) { return Math.pow(1 - x, 2) + 100 * Math.pow(y - x * x, 2); }
        @Override public double[] grad(double x, double y) {
            return new double[]{-2 * (1 - x) - 400 * x * (y - x * x), 200 * (y - x * x)};
        }
    };

    private final String label;
    private final String expr;
    private final double cx;
    private final double cy;

    SurfaceFunction(String label, String expr, double cx, double cy) {
        this.label = label;
        this.expr = expr;
        this.cx = cx;
        this.cy = cy;
    }

    public abstract double z(double x, double y);

    public abstract double[] grad(double x, double y);

    public String label() { return label; }

    public String expr() { return expr; }

    public double[] critical() { return new double[]{cx, cy}; }

    public static SurfaceFunction of(String key) {
        if (key != null) {
            for (SurfaceFunction f : values()) {
                if (f.name().equalsIgnoreCase(key)) return f;
            }
        }
        throw new InvalidParameterException("알 수 없는 함수: " + key);
    }
}
