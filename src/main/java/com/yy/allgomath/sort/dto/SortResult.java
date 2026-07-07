package com.yy.allgomath.sort.dto;

import java.util.List;

public record SortResult(List<SortEvent> events, int comparisons, int swaps, int writes, int[] sorted) {
}
