package com.yy.allgomath.dfs;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.dfs.dto.DfsEvent;
import com.yy.allgomath.dfs.dto.DfsParams;
import com.yy.allgomath.dfs.dto.DfsResult;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 격자 미로 DFS 백트래킹. push/pop 이벤트 trace와 도달 시 스택 경로를 one-shot으로 반환.
 * ORDER는 프로토타입(dfs-lab.js) 코드 그대로: [[0,1],[1,0],[0,-1],[-1,0]] consumed as [dc,dr],
 * 즉 nr=r+dr, nc=c+dc. 실제 탐색순서: down, right, up, left.
 */
@Service
public class DfsService implements Computer<DfsParams, DfsResult> {

    // ORDER from prototype: [dc, dr] pairs = [col-delta, row-delta]
    // (0,1)=down, (1,0)=right, (0,-1)=up, (-1,0)=left
    private static final int[][] ORDER = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    @Override
    public DfsResult compute(DfsParams params) {
        validate(params);

        int rows = params.rows(), cols = params.cols();
        boolean[] wall = params.walls();
        int start = params.start(), goal = params.goal();
        int n = rows * cols;

        boolean[] visited = new boolean[n];
        List<DfsEvent> events = new ArrayList<>();
        int deadEnds = 0;

        // stack holds explicit int values; Deque used as stack (push/pop at front)
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;
        events.add(new DfsEvent("push", start));

        if (start == goal) {
            return new DfsResult(events.toArray(new DfsEvent[0]), new int[]{start}, true, 0);
        }

        while (!stack.isEmpty()) {
            int cur = stack.peek();
            int r = cur / cols, c = cur % cols;
            boolean moved = false;

            for (int[] order : ORDER) {
                int dc = order[0], dr = order[1];
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int ni = nr * cols + nc;
                if (wall[ni] || visited[ni]) continue;

                visited[ni] = true;
                stack.push(ni);
                events.add(new DfsEvent("push", ni));
                moved = true;

                if (ni == goal) {
                    // path = live stack contents from start (bottom) to goal (top)
                    Integer[] stackArr = stack.toArray(new Integer[0]);
                    int[] path = new int[stackArr.length];
                    // ArrayDeque.toArray() returns elements in LIFO order (top first),
                    // so reverse to get start-to-goal order
                    for (int i = 0; i < stackArr.length; i++) {
                        path[i] = stackArr[stackArr.length - 1 - i];
                    }
                    return new DfsResult(events.toArray(new DfsEvent[0]), path, true, deadEnds);
                }
                break; // commit to this neighbour - depth first
            }

            if (!moved) {
                deadEnds++;
                events.add(new DfsEvent("pop", cur));
                stack.pop();
            }
        }

        // goal not reachable
        return new DfsResult(events.toArray(new DfsEvent[0]), new int[0], false, deadEnds);
    }

    private void validate(DfsParams p) {
        int rows = p.rows(), cols = p.cols();
        if (rows < 4 || rows > 40) throw new InvalidParameterException("rows는 4~40 사이여야 합니다.");
        if (cols < 4 || cols > 60) throw new InvalidParameterException("cols는 4~60 사이여야 합니다.");
        int n = rows * cols;
        if (n > 2400) throw new InvalidParameterException("그리드 크기는 2400 셀 이하여야 합니다.");
        boolean[] wall = p.walls();
        if (wall == null || wall.length != n) {
            throw new InvalidParameterException("walls 길이는 rows*cols와 같아야 합니다.");
        }
        int start = p.start(), goal = p.goal();
        if (start < 0 || start >= n || goal < 0 || goal >= n) {
            throw new InvalidParameterException("start/goal은 그리드 범위 안이어야 합니다.");
        }
        if (wall[start] || wall[goal]) {
            throw new InvalidParameterException("start/goal은 벽일 수 없습니다.");
        }
    }
}
