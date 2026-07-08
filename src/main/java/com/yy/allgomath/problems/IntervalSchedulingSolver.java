package com.yy.allgomath.problems;

import com.yy.allgomath.greedy.GreedyService;
import com.yy.allgomath.greedy.dto.Decision;
import com.yy.allgomath.greedy.dto.GreedyParams;
import com.yy.allgomath.greedy.dto.GreedyResult;
import com.yy.allgomath.greedy.dto.TaskInterval;
import com.yy.allgomath.problems.dto.ProblemSolveRequest;
import com.yy.allgomath.problems.dto.TraceEnvelope;
import com.yy.allgomath.problems.dto.TraceFrame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 기존 그리디 인터벌 스케줄링을 표준 envelope로 소급 노출하는 어댑터.
 * params: { strategy }  input: { tasks: [{ s, e }] }
 * frames: decide(idx,accepted,lastEnd) 결정 순서.
 */
@Component
@RequiredArgsConstructor
public class IntervalSchedulingSolver implements ProblemSolver {

    private final GreedyService greedyService;

    @Override
    public String problemId() {
        return "interval-scheduling";
    }

    @Override
    public TraceEnvelope solve(ProblemSolveRequest req) {
        List<Map<String, Object>> raw = Inputs.reqObjectList(req.inputOrEmpty(), "tasks", 1, 40);
        String strategy = Inputs.reqString(req.paramsOrEmpty(), "strategy");

        List<TaskInterval> tasks = new ArrayList<>(raw.size());
        for (Map<String, Object> t : raw) {
            tasks.add(new TaskInterval(
                    Inputs.reqDouble(t, "s", 0, 100),
                    Inputs.reqDouble(t, "e", 0, 100)));
        }

        GreedyResult r = greedyService.compute(new GreedyParams(tasks, strategy));

        List<TraceFrame> frames = new ArrayList<>();
        for (Decision d : r.decisions()) {
            frames.add(TraceFrame.of("decide",
                    Map.of("idx", d.idx(), "accepted", d.accepted(), "lastEnd", d.lastEnd())));
        }

        return new TraceEnvelope(Map.of("selected", r.selected(), "optimal", r.optimal()), frames);
    }
}
