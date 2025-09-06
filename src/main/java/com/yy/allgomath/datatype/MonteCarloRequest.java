package com.yy.allgomath.datatype;

public class MonteCarloRequest {
    private int iterations;
    private Bounds bounds;
    private String functionType;

    public static class Bounds {
        private double xMin;
        private double xMax;
        private double yMin;
        private double yMax;

        public Bounds() {}

        public Bounds(double xMin, double xMax, double yMin, double yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        public double getxMin() { return xMin; }
        public void setxMin(double xMin) { this.xMin = xMin; }
        public double getxMax() { return xMax; }
        public void setxMax(double xMax) { this.xMax = xMax; }
        public double getyMin() { return yMin; }
        public void setyMin(double yMin) { this.yMin = yMin; }
        public double getyMax() { return yMax; }
        public void setyMax(double yMax) { this.yMax = yMax; }
    }

    public MonteCarloRequest() {}

    public MonteCarloRequest(int iterations, Bounds bounds, String functionType) {
        this.iterations = iterations;
        this.bounds = bounds;
        this.functionType = functionType;
    }

    public int getIterations() { return iterations; }
    public void setIterations(int iterations) { this.iterations = iterations; }
    public Bounds getBounds() { return bounds; }
    public void setBounds(Bounds bounds) { this.bounds = bounds; }
    public String getFunctionType() { return functionType; }
    public void setFunctionType(String functionType) { this.functionType = functionType; }
}