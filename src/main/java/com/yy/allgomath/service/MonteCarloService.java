package com.yy.allgomath.service;

import com.yy.allgomath.datatype.MonteCarloPoint;
import com.yy.allgomath.datatype.MonteCarloRequest;
import com.yy.allgomath.datatype.MonteCarloResult;
import com.yy.allgomath.math.MathFunction;
import com.yy.allgomath.math.MathFunctionFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MonteCarloService {
    
    private final Random random = new Random();
    
    public MonteCarloResult performMonteCarloIntegration(MonteCarloRequest request) {
        MathFunction function = MathFunctionFactory.createFunction(request.getFunctionType());
        
        List<MonteCarloPoint> points = new ArrayList<>();
        List<Double> convergenceHistory = new ArrayList<>();
        
        double xMin = request.getBounds().getxMin();
        double xMax = request.getBounds().getxMax();
        double yMin = request.getBounds().getyMin();
        double yMax = request.getBounds().getyMax();
        
        double area = (xMax - xMin) * (yMax - yMin);
        int insideCount = 0;
        double sum = 0.0;
        
        for (int i = 0; i < request.getIterations(); i++) {
            double x = xMin + random.nextDouble() * (xMax - xMin);
            double y = yMin + random.nextDouble() * (yMax - yMin);
            
            try {
                double functionValue = function.evaluate(x, y);
                boolean inside = !Double.isNaN(functionValue) && functionValue >= 0;
                
                if (inside) {
                    insideCount++;
                    sum += functionValue;
                }
                
                points.add(new MonteCarloPoint(x, y, functionValue, inside));
                
                if ((i + 1) % 10 == 0) {
                    double currentEstimate = (sum / (i + 1)) * area;
                    convergenceHistory.add(currentEstimate);
                }
                
            } catch (Exception e) {
                points.add(new MonteCarloPoint(x, y, Double.NaN, false));
                
                if ((i + 1) % 10 == 0) {
                    double currentEstimate = (sum / (i + 1)) * area;
                    convergenceHistory.add(currentEstimate);
                }
            }
        }
        
        double estimate = (sum / request.getIterations()) * area;
        double actualValue = function.getActualIntegral(xMin, xMax, yMin, yMax);
        
        return new MonteCarloResult(points, estimate, actualValue, insideCount, 
                                  request.getIterations(), convergenceHistory);
    }
}