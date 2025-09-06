package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class CircleFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        double radius = 1.0;
        return Math.sqrt(radius * radius - x * x - y * y);
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        if (xMin == -1.0 && xMax == 1.0 && yMin == -1.0 && yMax == 1.0) {
            return (2.0 * Math.PI) / 3.0;
        }
        return Double.NaN;
    }
}