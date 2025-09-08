package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class SquareFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        // For 2D Monte Carlo: return 1 if inside region x² + y² ≤ 4, 0 if outside  
        double threshold = 4.0;
        return (x * x + y * y <= threshold) ? 1.0 : 0.0;
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        // Area of region x² + y² ≤ 4 (circle with radius 2) in given bounds
        // For [-2,2] x [-2,2] domain, this is the full circle area
        if (xMin == -2.0 && xMax == 2.0 && yMin == -2.0 && yMax == 2.0) {
            return Math.PI * 4.0; // Area of circle with radius 2: π * 2²
        }
        
        // For general case, calculate intersection area between circle and rectangle
        double radius = 2.0;
        double circleArea = Math.PI * radius * radius;
        
        // Simple approximation: if bounds contain the full circle, return full area
        if (xMin <= -radius && xMax >= radius && yMin <= -radius && yMax >= radius) {
            return circleArea;
        }
        
        // Otherwise, approximate based on bounds
        double effectiveRadius = Math.min(Math.min(xMax - xMin, yMax - yMin) / 2.0, radius);
        return Math.PI * effectiveRadius * effectiveRadius;
    }
}