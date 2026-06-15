package com.yy.allgomath.plotter.dto;

import java.util.List;

public record DescentResult(List<GradPoint> path, boolean converged, int iterations) {
}
