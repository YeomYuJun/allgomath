package com.yy.allgomath.problems;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.problems.dto.ProblemSolveRequest;
import com.yy.allgomath.problems.dto.TraceEnvelope;
import com.yy.allgomath.problems.dto.TraceFrame;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 강의실 배정 (백준 11000). 시작시각 오름차순 스캔 + 최소힙(강의실 종료시각)으로 최소 개수 배정.
 * input: { lectures: [{ s, e }] }
 * frames: assign(idx,room,reused) 강의별 배정 결정.
 */
@Component
public class ClassroomSolver implements ProblemSolver {

    private static final int MAX_LECTURES = 15;

    @Override
    public String problemId() {
        return "classroom";
    }

    @Override
    public TraceEnvelope solve(ProblemSolveRequest req) {
        List<Map<String, Object>> raw = Inputs.reqObjectList(req.inputOrEmpty(), "lectures", 1, MAX_LECTURES);

        int n = raw.size();
        double[] s = new double[n];
        double[] e = new double[n];
        for (int i = 0; i < n; i++) {
            s[i] = Inputs.reqDouble(raw.get(i), "s", 0, 100);
            e[i] = Inputs.reqDouble(raw.get(i), "e", 0, 100);
            if (s[i] >= e[i]) throw new InvalidParameterException("각 lecture는 s < e 여야 합니다.");
        }

        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        java.util.Arrays.sort(order, Comparator.<Integer>comparingDouble(i -> s[i]).thenComparingDouble(i -> e[i]));

        // 힙 원소: [종료시각, 강의실 번호]
        PriorityQueue<double[]> rooms = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));
        List<TraceFrame> frames = new ArrayList<>();
        int roomCount = 0;
        for (Integer idx : order) {
            int room;
            boolean reused = !rooms.isEmpty() && rooms.peek()[0] <= s[idx];
            if (reused) {
                room = (int) rooms.poll()[1];
            } else {
                room = roomCount++;
            }
            rooms.add(new double[]{e[idx], room});
            frames.add(TraceFrame.of("assign", Map.of("idx", idx, "room", room, "reused", reused)));
        }

        Map<String, Object> meta = Map.of("rooms", roomCount, "count", n);
        return new TraceEnvelope(meta, frames);
    }
}
