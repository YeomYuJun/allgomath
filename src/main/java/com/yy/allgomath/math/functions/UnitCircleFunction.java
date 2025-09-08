package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class UnitCircleFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        // Unit circle: inside if x^2 + y^2 <= 1
        return (x * x + y * y <= 1.0) ? 1.0 : 0.0;
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        // For a unit circle in [-1,1] x [-1,1] domain, the area is π
        if (xMin == -1.0 && xMax == 1.0 && yMin == -1.0 && yMax == 1.0) {
            return Math.PI;
        }
        // For general case, approximate using circle area formula
        double width = xMax - xMin;
        double height = yMax - yMin;
        double radius = Math.min(width, height) / 2.0;
        return Math.PI * radius * radius;
    }
}