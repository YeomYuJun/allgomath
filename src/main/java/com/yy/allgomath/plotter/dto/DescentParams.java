package com.yy.allgomath.plotter.dto;

public record DescentParams(String fn, double startX, double startY, double learningRate, int maxIterations,
                            boolean classic, double range) {

    public DescentParams(String fn, double startX, double startY, double learningRate, int maxIterations) {
        this(fn, startX, startY, learningRate, maxIterations, false, 0);
    }
}
