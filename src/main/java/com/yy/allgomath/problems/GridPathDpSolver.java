package com.yy.allgomath.problems;

import com.yy.allgomath.dp.DpService;
import com.yy.allgomath.dp.dto.DpParams;
import com.yy.allgomath.dp.dto.DpResult;
import com.yy.allgomath.problems.dto.ProblemSolveRequest;
import com.yy.allgomath.problems.dto.TraceEnvelope;
import com.yy.allgomath.problems.dto.TraceFrame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 기존 DP 격자 경로 문제를 표준 envelope로 소급 노출하는 어댑터.
 * params: { mode }  input: { grid }
 * frames: cell(r,c,value,from) 채우기 -> path(r,c) 역추적.
 */
@Component
@RequiredArgsConstructor
public class GridPathDpSolver implements ProblemSolver {

    private final DpService dpService;

    @Override
    public String problemId() {
        return "grid-path";
    }

    @Override
    public TraceEnvelope solve(ProblemSolveRequest req) {
        int[][] grid = Inputs.reqIntMatrix(req.inputOrEmpty(), "grid");
        String mode = Inputs.reqString(req.paramsOrEmpty(), "mode");

        DpResult r = dpService.compute(new DpParams(grid, mode));

        List<TraceFrame> frames = new ArrayList<>();
        for (int[] cell : r.fillOrder()) {
            int row = cell[0], col = cell[1];
            Map<String, Object> args = new HashMap<>();
            args.put("r", row);
            args.put("c", col);
            args.put("value", r.dp()[row][col]);
            args.put("from", r.from()[row][col]);
            frames.add(TraceFrame.of("cell", args));
        }
        for (int[] cell : r.path()) {
            frames.add(TraceFrame.of("path", Map.of("r", cell[0], "c", cell[1])));
        }

        return new TraceEnvelope(Map.of("best", r.best()), frames);
    }
}
