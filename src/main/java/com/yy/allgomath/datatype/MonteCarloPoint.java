package com.yy.allgomath.datatype;

public class MonteCarloPoint {
    private double x;
    private double y;
    private double functionValue;
    private boolean inside;

    public MonteCarloPoint() {}

    public MonteCarloPoint(double x, double y, double functionValue, boolean inside) {
        this.x = x;
        this.y = y;
        this.functionValue = functionValue;
        this.inside = inside;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getFunctionValue() { return functionValue; }
    public void setFunctionValue(double functionValue) { this.functionValue = functionValue; }
    public boolean isInside() { return inside; }
    public void setInside(boolean inside) { this.inside = inside; }
}