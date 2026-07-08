package com.yy.allgomath.problems;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.problems.dto.ProblemSolveRequest;
import com.yy.allgomath.problems.dto.TraceEnvelope;
import com.yy.allgomath.problems.dto.TraceFrame;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 0/1 배낭 (백준 12865 평범한 배낭).
 * params: { capacity }  input: { items: [{ w, v }] }
 * frames: cell(i,w,value,take) 테이블 채우기 -> pick(i,w) 역추적.
 */
@Component
public class KnapsackSolver implements ProblemSolver {

    private static final int MAX_ITEMS = 10;
    private static final int MAX_CAPACITY = 30;

    @Override
    public String problemId() {
        return "knapsack";
    }

    @Override
    public TraceEnvelope solve(ProblemSolveRequest req) {
        int capacity = Inputs.reqInt(req.paramsOrEmpty(), "capacity", 1, MAX_CAPACITY);
        List<Map<String, Object>> rawItems = Inputs.reqObjectList(req.inputOrEmpty(), "items", 1, MAX_ITEMS);

        int n = rawItems.size();
        int[] w = new int[n];
        int[] v = new int[n];
        for (int i = 0; i < n; i++) {
            w[i] = Inputs.reqInt(rawItems.get(i), "w", 1, MAX_CAPACITY);
            v[i] = Inputs.reqInt(rawItems.get(i), "v", 1, 99);
        }

        int[][] dp = new int[n + 1][capacity + 1];
        List<TraceFrame> frames = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= capacity; c++) {
                int without = dp[i - 1][c];
                boolean fits = w[i - 1] <= c;
                int with = fits ? dp[i - 1][c - w[i - 1]] + v[i - 1] : Integer.MIN_VALUE;
                boolean take = fits && with > without;
                dp[i][c] = take ? with : without;
                frames.add(TraceFrame.of("cell", Map.of("i", i, "w", c, "value", dp[i][c], "take", take)));
            }
        }

        List<Integer> picks = new ArrayList<>();
        int c = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][c] != dp[i - 1][c]) {
                picks.add(0, i - 1);
                frames.add(TraceFrame.of("pick", Map.of("i", i, "w", c)));
                c -= w[i - 1];
            }
        }
        if (c < 0) throw new InvalidParameterException("역추적이 유효하지 않습니다.");

        Map<String, Object> meta = Map.of(
                "best", dp[n][capacity],
                "picks", picks,
                "itemCount", n,
                "capacity", capacity);
        return new TraceEnvelope(meta, frames);
    }
}
