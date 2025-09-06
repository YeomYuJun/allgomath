package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class SquareFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        return x * x + y * y;
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        double xIntegral = (Math.pow(xMax, 3) - Math.pow(xMin, 3)) / 3.0;
        double yIntegral = (Math.pow(yMax, 3) - Math.pow(yMin, 3)) / 3.0;
        return xIntegral * (yMax - yMin) + yIntegral * (xMax - xMin);
    }
}