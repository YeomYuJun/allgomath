package com.yy.allgomath.greedy;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.greedy.dto.Decision;
import com.yy.allgomath.greedy.dto.GreedyParams;
import com.yy.allgomath.greedy.dto.GreedyResult;
import com.yy.allgomath.greedy.dto.TaskInterval;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** 인터벌 스케줄링(활동 선택) 그리디 연산. */
@Service
public class GreedyService implements Computer<GreedyParams, GreedyResult> {

    private static final Set<String> VALID_STRATEGIES = Set.of("finish", "start", "shortest");

    @Override
    public GreedyResult compute(GreedyParams params) {
        validate(params);
        StrategyRun r = runStrategy(params.tasks(), params.strategy());
        int optimal = runStrategy(params.tasks(), "finish").selected;
        return new GreedyResult(r.order, r.decisions, r.selected, optimal);
    }

    private StrategyRun runStrategy(List<TaskInterval> tasks, String strategy) {
        int n = tasks.size();
        Comparator<Integer> cmp = keyFn(tasks, strategy);

        // Box to Integer[] for stable TimSort (Arrays.sort on int[] has no comparator)
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, cmp);

        // Unbox order to int[]
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = idx[i];

        Decision[] decisions = new Decision[n];
        double lastEnd = -1;
        int selected = 0;
        for (int k = 0; k < n; k++) {
            int i = order[k];
            boolean ok = tasks.get(i).s() >= lastEnd;
            if (ok) {
                lastEnd = tasks.get(i).e();
                selected++;
            }
            // lastEnd recorded AFTER potential update (rejected tasks record unchanged lastEnd)
            decisions[k] = new Decision(i, ok, lastEnd);
        }
        return new StrategyRun(order, decisions, selected);
    }

    private Comparator<Integer> keyFn(List<TaskInterval> tasks, String strategy) {
        return switch (strategy) {
            case "finish"   -> Comparator.comparingDouble(i -> tasks.get(i).e());
            case "start"    -> Comparator.comparingDouble(i -> tasks.get(i).s());
            case "shortest" -> Comparator.comparingDouble(i -> tasks.get(i).e() - tasks.get(i).s());
            default         -> throw new InvalidParameterException("strategy는 finish/start/shortest 중 하나여야 합니다.");
        };
    }

    private void validate(GreedyParams p) {
        List<TaskInterval> tasks = p.tasks();
        if (tasks == null || tasks.isEmpty()) throw new InvalidParameterException("tasks는 1개 이상이어야 합니다.");
        if (tasks.size() > 40) throw new InvalidParameterException("tasks는 최대 40개까지 허용됩니다.");
        for (TaskInterval t : tasks) {
            if (t.s() < 0 || t.e() > 100 || t.s() >= t.e()) {
                throw new InvalidParameterException("각 task는 0 <= s < e <= 100 이어야 합니다.");
            }
        }
        if (!VALID_STRATEGIES.contains(p.strategy())) {
            throw new InvalidParameterException("strategy는 finish/start/shortest 중 하나여야 합니다.");
        }
    }

    /** Internal transport for runStrategy result. */
    private record StrategyRun(int[] order, Decision[] decisions, int selected) {}
}
