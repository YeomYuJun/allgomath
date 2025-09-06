package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class SinProductFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        return Math.sin(x * y);
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        if (Math.abs(xMin) == Math.abs(xMax) && Math.abs(yMin) == Math.abs(yMax)) {
            return 0.0;
        }
        return Double.NaN;
    }
}