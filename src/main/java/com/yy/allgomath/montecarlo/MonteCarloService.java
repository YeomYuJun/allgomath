package com.yy.allgomath.montecarlo;

import com.yy.allgomath.montecarlo.dto.MonteCarloPoint;
import com.yy.allgomath.montecarlo.dto.MonteCarloRequest;
import com.yy.allgomath.montecarlo.dto.MonteCarloResult;
import com.yy.allgomath.montecarlo.function.MathFunction;
import com.yy.allgomath.montecarlo.function.MathFunctionFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MonteCarloService {

    // 응답에 담는 시각화 포인트 상한. 추정 정확도는 전 반복으로 유지하고 포인트만 균등 샘플링한다.
    public static final int MAX_POINTS = 5000;

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
        int pointStride = Math.max(1, request.getIterations() / MAX_POINTS);

        for (int i = 0; i < request.getIterations(); i++) {
            double x = xMin + ThreadLocalRandom.current().nextDouble() * (xMax - xMin);
            double y = yMin + ThreadLocalRandom.current().nextDouble() * (yMax - yMin);
            boolean samplePoint = (i % pointStride == 0);

            try {
                double functionValue = function.evaluate(x, y);
                boolean inside = determineInside(request.getFunctionType(), x, y, functionValue);

                if (inside) {
                    insideCount++;
                    if (!Double.isNaN(functionValue)) {
                        sum += functionValue;
                    }
                }

                if (samplePoint) {
                    points.add(new MonteCarloPoint(x, y, functionValue, inside));
                }

                if ((i + 1) % 10 == 0) {
                    double currentEstimate = ((double) insideCount / (i + 1)) * area;
                    convergenceHistory.add(currentEstimate);
                }

            } catch (Exception e) {
                if (samplePoint) {
                    points.add(new MonteCarloPoint(x, y, Double.NaN, false));
                }

                if ((i + 1) % 10 == 0) {
                    double currentEstimate = ((double) insideCount / (i + 1)) * area;
                    convergenceHistory.add(currentEstimate);
                }
            }
        }

        double estimate = ((double) insideCount / request.getIterations()) * area;
        double actualValue = function.getActualIntegral(xMin, xMax, yMin, yMax);

        return new MonteCarloResult(points, estimate, actualValue, insideCount,
                                  request.getIterations(), convergenceHistory);
    }

    private boolean determineInside(String functionType, double x, double y, double functionValue) {
        switch (functionType.toLowerCase()) {
            case "unit_circle":
                // For unit circle, inside if function returns 1 (meaning x^2 + y^2 <= 1)
                return functionValue == 1.0;

            case "circle":
                // Circle is now handled by SquareFunction (x^2 + y^2 <= 4)
                return functionValue == 1.0;

            case "sin_product":
                // For sin(x*y), we can consider positive values as "inside" for visualization
                return !Double.isNaN(functionValue) && functionValue >= 0;

            case "square":
                // For x^2 + y^2 <= 4, inside if function returns 1
                return functionValue == 1.0;

            case "ellipse":
                // For ellipse x^2/4 + y^2 <= 1, inside if function returns 1
                return functionValue == 1.0;

            case "diamond":
                // For diamond |x| + |y| <= 2, inside if function returns 1
                return functionValue == 1.0;

            default:
                // Default behavior: inside if not NaN and >= 0
                return !Double.isNaN(functionValue) && functionValue >= 0;
        }
    }
}
