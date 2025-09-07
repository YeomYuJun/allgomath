package com.yy.allgomath.service;

import com.yy.allgomath.datatype.BezierRequest;
import com.yy.allgomath.datatype.BezierResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BezierService {

    public BezierResult generateBezierCurve(BezierRequest request) {
        List<BezierRequest.Point2D> controlPoints = request.getControlPoints();
        if (controlPoints == null || controlPoints.size() < 2) {
            throw new IllegalArgumentException("At least 2 control points are required");
        }

        int degree = controlPoints.size() - 1;
        List<BezierRequest.Point2D> curvePoints = new ArrayList<>();
        List<List<BezierRequest.Point2D>> constructionLines = new ArrayList<>();
        List<BezierRequest.Point2D> intermediatePoints = new ArrayList<>();
        
        // Generate curve points using De Casteljau algorithm
        for (int i = 0; i <= request.getResolution(); i++) {
            double t = (double) i / request.getResolution();
            BezierRequest.Point2D point = deCasteljauPoint(controlPoints, t);
            curvePoints.add(point);
        }

        // Generate construction lines and intermediate points for specific t
        if (request.isShowConstructionLines()) {
            double t = request.getTParameter();
            List<List<BezierRequest.Point2D>> construction = deCasteljauConstruction(controlPoints, t);
            constructionLines = construction;
            
            // Get intermediate points from construction process
            for (List<BezierRequest.Point2D> level : construction) {
                intermediatePoints.addAll(level);
            }
        }

        // Calculate current point for the given t parameter
        BezierRequest.Point2D currentPoint = deCasteljauPoint(controlPoints, request.getTParameter());

        return new BezierResult(curvePoints, constructionLines, intermediatePoints, 
                               currentPoint, request.getTParameter(), degree);
    }

    private BezierRequest.Point2D deCasteljauPoint(List<BezierRequest.Point2D> controlPoints, double t) {
        List<BezierRequest.Point2D> points = new ArrayList<>(controlPoints);
        
        for (int level = 0; level < controlPoints.size() - 1; level++) {
            for (int i = 0; i < points.size() - 1; i++) {
                BezierRequest.Point2D p0 = points.get(i);
                BezierRequest.Point2D p1 = points.get(i + 1);
                
                double x = (1 - t) * p0.getX() + t * p1.getX();
                double y = (1 - t) * p0.getY() + t * p1.getY();
                
                points.set(i, new BezierRequest.Point2D(x, y));
            }
            points.remove(points.size() - 1);
        }
        
        return points.get(0);
    }

    private List<List<BezierRequest.Point2D>> deCasteljauConstruction(List<BezierRequest.Point2D> controlPoints, double t) {
        List<List<BezierRequest.Point2D>> construction = new ArrayList<>();
        List<BezierRequest.Point2D> currentLevel = new ArrayList<>(controlPoints);
        
        // Add initial control points
        construction.add(new ArrayList<>(currentLevel));
        
        for (int level = 0; level < controlPoints.size() - 1; level++) {
            List<BezierRequest.Point2D> nextLevel = new ArrayList<>();
            
            for (int i = 0; i < currentLevel.size() - 1; i++) {
                BezierRequest.Point2D p0 = currentLevel.get(i);
                BezierRequest.Point2D p1 = currentLevel.get(i + 1);
                
                double x = (1 - t) * p0.getX() + t * p1.getX();
                double y = (1 - t) * p0.getY() + t * p1.getY();
                
                nextLevel.add(new BezierRequest.Point2D(x, y));
            }
            
            construction.add(new ArrayList<>(nextLevel));
            currentLevel = nextLevel;
        }
        
        return construction;
    }

    public BezierResult getBezierDerivative(BezierRequest request) {
        List<BezierRequest.Point2D> controlPoints = request.getControlPoints();
        if (controlPoints == null || controlPoints.size() < 2) {
            throw new IllegalArgumentException("At least 2 control points are required");
        }

        // Calculate derivative control points
        List<BezierRequest.Point2D> derivativePoints = new ArrayList<>();
        int n = controlPoints.size() - 1;
        
        for (int i = 0; i < n; i++) {
            BezierRequest.Point2D p0 = controlPoints.get(i);
            BezierRequest.Point2D p1 = controlPoints.get(i + 1);
            
            double x = n * (p1.getX() - p0.getX());
            double y = n * (p1.getY() - p0.getY());
            
            derivativePoints.add(new BezierRequest.Point2D(x, y));
        }

        // Generate derivative curve
        BezierRequest derivativeRequest = new BezierRequest(derivativePoints, request.getResolution(), 
                                                           request.getTParameter(), false);
        return generateBezierCurve(derivativeRequest);
    }

    public double calculateCurveLength(List<BezierRequest.Point2D> curvePoints) {
        double length = 0.0;
        for (int i = 1; i < curvePoints.size(); i++) {
            BezierRequest.Point2D p0 = curvePoints.get(i - 1);
            BezierRequest.Point2D p1 = curvePoints.get(i);
            
            double dx = p1.getX() - p0.getX();
            double dy = p1.getY() - p0.getY();
            length += Math.sqrt(dx * dx + dy * dy);
        }
        return length;
    }

    public List<BezierRequest.Point2D> elevateDegree(List<BezierRequest.Point2D> controlPoints) {
        int n = controlPoints.size();
        List<BezierRequest.Point2D> elevatedPoints = new ArrayList<>();
        
        // First point remains the same
        elevatedPoints.add(new BezierRequest.Point2D(controlPoints.get(0).getX(), controlPoints.get(0).getY()));
        
        // Intermediate points
        for (int i = 1; i < n; i++) {
            BezierRequest.Point2D prev = controlPoints.get(i - 1);
            BezierRequest.Point2D curr = controlPoints.get(i);
            
            double ratio = (double) i / n;
            double x = ratio * prev.getX() + (1 - ratio) * curr.getX();
            double y = ratio * prev.getY() + (1 - ratio) * curr.getY();
            
            elevatedPoints.add(new BezierRequest.Point2D(x, y));
        }
        
        // Last point remains the same
        elevatedPoints.add(new BezierRequest.Point2D(controlPoints.get(n - 1).getX(), controlPoints.get(n - 1).getY()));
        
        return elevatedPoints;
    }
}