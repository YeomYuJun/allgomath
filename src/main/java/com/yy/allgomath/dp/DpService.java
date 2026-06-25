package com.yy.allgomath.dp;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.dp.dto.DpParams;
import com.yy.allgomath.dp.dto.DpResult;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** DP 격자 경로 최적화. 좌상->우하, 이동은 오른쪽/아래만 허용. max/min 모드 지원. */
@Service
public class DpService implements Computer<DpParams, DpResult> {

    @Override
    public DpResult compute(DpParams params) {
        validate(params);
        int[][] grid = params.grid();
        String mode = params.mode();
        int n = grid.length;

        int[][] dp = new int[n][n];
        String[][] from = new String[n][n];
        List<int[]> fillOrderList = new ArrayList<>(n * n);

        boolean isMax = mode.equals("max");

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Integer up   = (r > 0) ? dp[r - 1][c] : null;
                Integer left = (c > 0) ? dp[r][c - 1] : null;

                int best;
                String src = null;

                if (up == null && left == null) {
                    best = 0;
                } else if (up == null) {
                    best = left;
                    src = "left";
                } else if (left == null) {
                    best = up;
                    src = "up";
                } else {
                    boolean upWins = isMax ? (up >= left) : (up <= left);
                    if (upWins) {
                        best = up;
                        src = "up";
                    } else {
                        best = left;
                        src = "left";
                    }
                }

                dp[r][c] = best + grid[r][c];
                from[r][c] = src;
                fillOrderList.add(new int[]{r, c});
            }
        }

        // Trace path from (n-1,n-1) back to (0,0)
        List<int[]> pathList = new ArrayList<>();
        int r = n - 1, c = n - 1;
        while (r >= 0 && c >= 0) {
            pathList.add(new int[]{r, c});
            String s = from[r][c];
            if ("up".equals(s)) {
                r--;
            } else if ("left".equals(s)) {
                c--;
            } else {
                break;
            }
        }
        // Reverse to get top-left -> bottom-right order
        int pathSize = pathList.size();
        int[][] path = new int[pathSize][];
        for (int i = 0; i < pathSize; i++) {
            path[i] = pathList.get(pathSize - 1 - i);
        }

        int[][] fillOrder = fillOrderList.toArray(new int[0][]);
        int best = dp[n - 1][n - 1];

        return new DpResult(dp, from, fillOrder, path, best);
    }

    private void validate(DpParams p) {
        int[][] grid = p.grid();
        if (grid == null) throw new InvalidParameterException("grid는 null일 수 없습니다.");
        int n = grid.length;
        if (n < 2 || n > 12) throw new InvalidParameterException("grid 크기는 2~12 사이여야 합니다.");
        for (int r = 0; r < n; r++) {
            if (grid[r] == null || grid[r].length != n) {
                throw new InvalidParameterException("grid는 정사각형(n×n)이어야 하며 행이 null이면 안 됩니다.");
            }
            for (int c = 0; c < n; c++) {
                if (grid[r][c] < 1) throw new InvalidParameterException("grid의 모든 값은 1 이상이어야 합니다.");
            }
        }
        String mode = p.mode();
        if (!"max".equals(mode) && !"min".equals(mode)) {
            throw new InvalidParameterException("mode는 \"max\" 또는 \"min\"이어야 합니다.");
        }
    }
}
