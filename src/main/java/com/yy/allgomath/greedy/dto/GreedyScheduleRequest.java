package com.yy.allgomath.greedy.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GreedyScheduleRequest(
        @NotNull List<TaskInterval> tasks,
        @NotNull String strategy) {
}
