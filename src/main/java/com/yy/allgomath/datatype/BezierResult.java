package com.yy.allgomath.datatype;

import java.util.List;

public class BezierResult {
    private List<BezierRequest.Point2D> curvePoints;
    private List<List<BezierRequest.Point2D>> constructionLines;
    private List<BezierRequest.Point2D> intermediatePoints;
    private BezierRequest.Point2D currentPoint;
    private double currentT;
    private int degree;

    public BezierResult() {}

    public BezierResult(List<BezierRequest.Point2D> curvePoints, 
                       List<List<BezierRequest.Point2D>> constructionLines,
                       List<BezierRequest.Point2D> intermediatePoints,
                       BezierRequest.Point2D currentPoint,
                       double currentT,
                       int degree) {
        this.curvePoints = curvePoints;
        this.constructionLines = constructionLines;
        this.intermediatePoints = intermediatePoints;
        this.currentPoint = currentPoint;
        this.currentT = currentT;
        this.degree = degree;
    }

    public List<BezierRequest.Point2D> getCurvePoints() { return curvePoints; }
    public void setCurvePoints(List<BezierRequest.Point2D> curvePoints) { this.curvePoints = curvePoints; }
    public List<List<BezierRequest.Point2D>> getConstructionLines() { return constructionLines; }
    public void setConstructionLines(List<List<BezierRequest.Point2D>> constructionLines) { this.constructionLines = constructionLines; }
    public List<BezierRequest.Point2D> getIntermediatePoints() { return intermediatePoints; }
    public void setIntermediatePoints(List<BezierRequest.Point2D> intermediatePoints) { this.intermediatePoints = intermediatePoints; }
    public BezierRequest.Point2D getCurrentPoint() { return currentPoint; }
    public void setCurrentPoint(BezierRequest.Point2D currentPoint) { this.currentPoint = currentPoint; }
    public double getCurrentT() { return currentT; }
    public void setCurrentT(double currentT) { this.currentT = currentT; }
    public int getDegree() { return degree; }
    public void setDegree(int degree) { this.degree = degree; }
}