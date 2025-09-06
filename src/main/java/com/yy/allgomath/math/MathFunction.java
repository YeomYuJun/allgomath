package com.yy.allgomath.math;

public interface MathFunction {
    double evaluate(double x, double y);
    double getActualIntegral(double xMin, double xMax, double yMin, double yMax);
}