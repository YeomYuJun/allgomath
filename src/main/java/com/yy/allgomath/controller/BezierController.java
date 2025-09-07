package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.BezierRequest;
import com.yy.allgomath.datatype.BezierResult;
import com.yy.allgomath.service.BezierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bezier")
public class BezierController {

    @Autowired
    private BezierService bezierService;

    @PostMapping("/generate")
    public ResponseEntity<BezierResult> generateCurve(@RequestBody BezierRequest request) {
        try {
            BezierResult result = bezierService.generateBezierCurve(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/derivative")
    public ResponseEntity<BezierResult> getDerivative(@RequestBody BezierRequest request) {
        try {
            BezierResult result = bezierService.getBezierDerivative(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/elevate")
    public ResponseEntity<List<BezierRequest.Point2D>> elevateDegree(@RequestBody List<BezierRequest.Point2D> controlPoints) {
        try {
            List<BezierRequest.Point2D> elevatedPoints = bezierService.elevateDegree(controlPoints);
            return ResponseEntity.ok(elevatedPoints);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/length")
    public ResponseEntity<Double> calculateLength(@RequestBody List<BezierRequest.Point2D> curvePoints) {
        try {
            double length = bezierService.calculateCurveLength(curvePoints);
            return ResponseEntity.ok(length);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}