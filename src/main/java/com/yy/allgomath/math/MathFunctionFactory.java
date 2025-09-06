package com.yy.allgomath.math;

import com.yy.allgomath.math.functions.CircleFunction;
import com.yy.allgomath.math.functions.SinProductFunction;
import com.yy.allgomath.math.functions.SquareFunction;

public class MathFunctionFactory {
    
    public static MathFunction createFunction(String functionType) {
        switch (functionType.toLowerCase()) {
            case "square":
                return new SquareFunction();
            case "sin_product":
                return new SinProductFunction();
            case "circle":
                return new CircleFunction();
            default:
                return new SquareFunction();
        }
    }
}