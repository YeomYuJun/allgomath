package com.yy.allgomath.bfs.dto;

public record BfsResult(int[] order, int[] dist, int[] parent, int[] path, boolean found) {
}
