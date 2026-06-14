package com.yy.allgomath.automata;

import com.yy.allgomath.common.exception.InvalidParameterException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Conway Game of Life (B3/S23), 경계 유한 그리드(격자 밖은 죽음). */
@Service
public class LifeService {

    private static final int MAX_DIM = 120;

    /** 입력 그리드에서 다음 steps 세대를 차례로 계산하여 반환(입력 미포함). */
    public List<boolean[][]> simulate(boolean[][] grid, int steps) {
        validate(grid);
        List<boolean[][]> result = new ArrayList<>(steps);
        boolean[][] current = grid;
        for (int i = 0; i < steps; i++) {
            current = nextGeneration(current);
            result.add(current);
        }
        return result;
    }

    /** 한 세대 진행. */
    public boolean[][] nextGeneration(boolean[][] grid) {
        int h = grid.length, w = grid[0].length;
        boolean[][] next = new boolean[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                int n = liveNeighbors(grid, r, c, h, w);
                next[r][c] = grid[r][c] ? (n == 2 || n == 3) : (n == 3);
            }
        }
        return next;
    }

    public int population(boolean[][] grid) {
        int p = 0;
        for (boolean[] row : grid) for (boolean cell : row) if (cell) p++;
        return p;
    }

    private int liveNeighbors(boolean[][] g, int r, int c, int h, int w) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (nr >= 0 && nr < h && nc >= 0 && nc < w && g[nr][nc]) count++;
            }
        }
        return count;
    }

    private void validate(boolean[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            throw new InvalidParameterException("그리드가 비어 있습니다.");
        }
        int w = grid[0].length;
        if (grid.length > MAX_DIM || w > MAX_DIM) {
            throw new InvalidParameterException("그리드 크기는 최대 " + MAX_DIM + "x" + MAX_DIM + "입니다.");
        }
        for (boolean[] row : grid) {
            if (row == null || row.length != w) {
                throw new InvalidParameterException("그리드의 모든 행은 같은 길이여야 합니다.");
            }
        }
    }
}
