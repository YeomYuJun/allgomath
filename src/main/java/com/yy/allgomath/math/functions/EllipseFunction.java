package com.yy.allgomath.math.functions;

import com.yy.allgomath.math.MathFunction;

public class EllipseFunction implements MathFunction {
    
    @Override
    public double evaluate(double x, double y) {
        // For 2D Monte Carlo: return 1 if inside ellipse, 0 if outside
        // Ellipse: x²/4 + y² ≤ 1 (horizontal radius=2, vertical radius=1)
        return (x * x / 4.0 + y * y / 1.0 <= 1.0) ? 1.0 : 0.0;
    }
    
    @Override
    public double getActualIntegral(double xMin, double xMax, double yMin, double yMax) {
        // Area of ellipse with semi-major axis a=2, semi-minor axis b=1: π*a*b = π*2*1 = 2π
        double a = 2.0; // horizontal radius
        double b = 1.0; // vertical radius
        double ellipseArea = Math.PI * a * b;
        
        // For [-2,2] x [-1,1] domain, this is the full ellipse area
        if (xMin <= -a && xMax >= a && yMin <= -b && yMax >= b) {
            return ellipseArea;
        }
        
        // For general case, approximate intersection area
        // Simple approximation: scale by the fraction of bounds that contain the ellipse
        double xCoverage = Math.min(1.0, (Math.min(xMax, a) - Math.max(xMin, -a)) / (2 * a));
        double yCoverage = Math.min(1.0, (Math.min(yMax, b) - Math.max(yMin, -b)) / (2 * b));
        
        if (xCoverage <= 0 || yCoverage <= 0) {
            return 0.0;
        }
        
        return ellipseArea * xCoverage * yCoverage;
    }
}