package com.yy.allgomath.bfs;

import com.yy.allgomath.bfs.dto.BfsParams;
import com.yy.allgomath.bfs.dto.BfsResult;
import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 격자 미로 BFS 최단경로. 전체 trace(방문순서/거리/부모/경로)를 one-shot으로 반환. */
@Service
public class BfsService implements Computer<BfsParams, BfsResult> {

    private static final int[][] N4 = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int[][] N8 = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    @Override
    public BfsResult compute(BfsParams params) {
        validate(params);
        int rows = params.rows(), cols = params.cols();
        boolean[] wall = params.walls();
        int start = params.start(), goal = params.goal();
        int n = rows * cols;

        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, -1);
        Arrays.fill(parent, -1);

        int[] order = new int[n];
        int orderLen = 0;
        int[] queue = new int[n];
        int head = 0, tail = 0;
        int[][] neigh = params.diag() ? N8 : N4;

        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int cur = queue[head++];
            order[orderLen++] = cur;
            int r = cur / cols, c = cur % cols;
            for (int[] d : neigh) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int ni = nr * cols + nc;
                if (wall[ni] || dist[ni] >= 0) continue;
                dist[ni] = dist[cur] + 1;
                parent[ni] = cur;
                queue[tail++] = ni;
            }
        }

        boolean found = dist[goal] >= 0;
        int[] path = new int[0];
        if (found) {
            List<Integer> rev = new ArrayList<>();
            for (int c = goal; c != -1; c = parent[c]) rev.add(c);
            path = new int[rev.size()];
            for (int i = 0; i < path.length; i++) path[i] = rev.get(path.length - 1 - i);
        }
        return new BfsResult(Arrays.copyOf(order, orderLen), dist, parent, path, found);
    }

    private void validate(BfsParams p) {
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
