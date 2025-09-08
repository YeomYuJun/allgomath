package com.yy.allgomath.math;

import com.yy.allgomath.math.functions.DiamondFunction;
import com.yy.allgomath.math.functions.EllipseFunction;
import com.yy.allgomath.math.functions.SinProductFunction;
import com.yy.allgomath.math.functions.SquareFunction;
import com.yy.allgomath.math.functions.UnitCircleFunction;

public class MathFunctionFactory {
    
    public static MathFunction createFunction(String functionType) {
        switch (functionType.toLowerCase()) {
            case "square":
                return new SquareFunction();
            case "sin_product":
                return new SinProductFunction();
            case "circle":
                return new SquareFunction(); // Circle is now handled by SquareFunction
            case "unit_circle":
                return new UnitCircleFunction();
            case "ellipse":
                return new EllipseFunction();
            case "diamond":
                return new DiamondFunction();
            default:
                return new SquareFunction();
        }
    }
}