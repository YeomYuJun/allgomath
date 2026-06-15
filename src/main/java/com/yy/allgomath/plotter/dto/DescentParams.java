package com.yy.allgomath.plotter.dto;

public record DescentParams(String fn, double startX, double startY, double learningRate, int maxIterations) {
}
