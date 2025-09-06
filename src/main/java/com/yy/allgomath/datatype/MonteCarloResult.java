package com.yy.allgomath.datatype;

import java.util.List;

public class MonteCarloResult {
    private List<MonteCarloPoint> points;
    private double estimate;
    private double actualValue;
    private int insideCount;
    private int totalCount;
    private List<Double> convergenceHistory;

    public MonteCarloResult() {}

    public MonteCarloResult(List<MonteCarloPoint> points, double estimate, double actualValue, 
                           int insideCount, int totalCount, List<Double> convergenceHistory) {
        this.points = points;
        this.estimate = estimate;
        this.actualValue = actualValue;
        this.insideCount = insideCount;
        this.totalCount = totalCount;
        this.convergenceHistory = convergenceHistory;
    }

    public List<MonteCarloPoint> getPoints() { return points; }
    public void setPoints(List<MonteCarloPoint> points) { this.points = points; }
    public double getEstimate() { return estimate; }
    public void setEstimate(double estimate) { this.estimate = estimate; }
    public double getActualValue() { return actualValue; }
    public void setActualValue(double actualValue) { this.actualValue = actualValue; }
    public int getInsideCount() { return insideCount; }
    public void setInsideCount(int insideCount) { this.insideCount = insideCount; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public List<Double> getConvergenceHistory() { return convergenceHistory; }
    public void setConvergenceHistory(List<Double> convergenceHistory) { this.convergenceHistory = convergenceHistory; }
}