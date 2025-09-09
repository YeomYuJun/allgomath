package com.yy.allgomath.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BezierRequest {
    @JsonProperty("controlPoints")
    private List<Point2D> controlPoints;
    @JsonProperty("resolution")
    private int resolution;
    @JsonProperty("tParameter")
    private double tParameter;
    @JsonProperty("showConstructionLines")
    private boolean showConstructionLines;

    public static class Point2D {
        @JsonProperty("x")
        private double x;
        @JsonProperty("y")
        private double y;

        public Point2D() {}

        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }

    public BezierRequest() {}

    public BezierRequest(List<Point2D> controlPoints, int resolution, double tParameter, boolean showConstructionLines) {
        this.controlPoints = controlPoints;
        this.resolution = resolution;
        this.tParameter = tParameter;
        this.showConstructionLines = showConstructionLines;
    }

    public List<Point2D> getControlPoints() { return controlPoints; }
    public void setControlPoints(List<Point2D> controlPoints) { this.controlPoints = controlPoints; }
    public int getResolution() { return resolution; }
    public void setResolution(int resolution) { this.resolution = resolution; }
    public double getTParameter() { return tParameter; }
    public void setTParameter(double tParameter) { this.tParameter = tParameter; }
    public boolean isShowConstructionLines() { return showConstructionLines; }
    public void setShowConstructionLines(boolean showConstructionLines) { this.showConstructionLines = showConstructionLines; }
}