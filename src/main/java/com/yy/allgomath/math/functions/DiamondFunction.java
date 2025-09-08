package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class DiamondFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        // For 2D Monte Carlo: return 1 if inside diamond, 0 if outside
        // Diamond: |x| + |y| ≤ 2 (Manhattan distance)
        return (Math.abs(x) + Math.abs(y) <= 2.0) ? 1.0 : 0.0;
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        // Area of diamond |x| + |y| ≤ 2 is 2 * 2² = 8
        // This forms a square rotated 45° with diagonal = 4, so area = (4²)/2 = 8
        double diamondArea = 8.0;
        
        // For [-2,2] x [-2,2] domain, this is the full diamond area
        if (xMin <= -2 && xMax >= 2 && yMin <= -2 && yMax >= 2) {
            return diamondArea;
        }
        
        // For general case, approximate intersection area
        // Diamond vertices are at (±2,0), (0,±2)
        double maxExtent = 2.0;
        
        if (xMin >= maxExtent || xMax <= -maxExtent || yMin >= maxExtent || yMax <= -maxExtent) {
            return 0.0;
        }
        
        // Simple approximation: scale by bounds coverage
        double xRange = Math.min(xMax, maxExtent) - Math.max(xMin, -maxExtent);
        double yRange = Math.min(yMax, maxExtent) - Math.max(yMin, -maxExtent);
        double totalRange = 4.0; // full range is -2 to 2
        
        // Rough approximation
        return diamondArea * (xRange / totalRange) * (yRange / totalRange);
    }
}