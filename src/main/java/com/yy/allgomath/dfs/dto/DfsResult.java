package com.yy.allgomath.dfs.dto;

public record DfsResult(DfsEvent[] events, int[] path, boolean found, int deadEnds) {
}
