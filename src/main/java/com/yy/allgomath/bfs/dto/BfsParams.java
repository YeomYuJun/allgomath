package com.yy.allgomath.bfs.dto;

public record BfsParams(int rows, int cols, boolean[] walls, int start, int goal, boolean diag) {
}
