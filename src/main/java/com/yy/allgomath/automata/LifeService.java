package com.yy.allgomath.automata;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.simulation.BatchSimulator;
import com.yy.allgomath.simulation.SimulationResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 라이프류 셀룰러 오토마타. 기본 규칙 B3/S23(Conway), birth/survive 마스크로 임의 규칙 지원. 경계 유한 그리드(격자 밖은 죽음). */
@Service
public class LifeService implements BatchSimulator<boolean[][], boolean[][]> {

    private static final int MAX_DIM = 120;
    private static final int MAX_FRAMES = 60;
    private static final boolean[] DEFAULT_BIRTH = mask(3);
    private static final boolean[] DEFAULT_SURVIVE = mask(2, 3);

    @Override
    public SimulationResponse<boolean[][]> simulate(boolean[][] grid, int steps) {
        return simulate(grid, steps, null, null);
    }

    public SimulationResponse<boolean[][]> simulate(boolean[][] grid, int steps, int[] birth, int[] survive) {
        validate(grid);
        boolean[] b = toMask(birth, DEFAULT_BIRTH);
        boolean[] s = toMask(survive, DEFAULT_SURVIVE);
        List<boolean[][]> result = new ArrayList<>(steps);
        double[] series = new double[steps];
        boolean[][] current = grid;
        for (int i = 0; i < steps; i++) {
            current = nextGeneration(current, b, s);
            result.add(current);
            series[i] = population(current);
        }
        return SimulationResponse.capped(result, series, MAX_FRAMES);
    }

    public boolean[][] nextGeneration(boolean[][] grid) {
        return nextGeneration(grid, DEFAULT_BIRTH, DEFAULT_SURVIVE);
    }

    public boolean[][] nextGeneration(boolean[][] grid, boolean[] birth, boolean[] survive) {
        int h = grid.length, w = grid[0].length;
        boolean[][] next = new boolean[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                int n = liveNeighbors(grid, r, c, h, w);
                next[r][c] = grid[r][c] ? survive[n] : birth[n];
            }
        }
        return next;
    }

    private static boolean[] mask(int... counts) {
        boolean[] m = new boolean[9];
        for (int n : counts) m[n] = true;
        return m;
    }

    /** null이면 기본 규칙, 빈 배열은 "어떤 이웃 수에서도 불가"라는 유효한 규칙(예: Seeds의 S). */
    private static boolean[] toMask(int[] counts, boolean[] def) {
        if (counts == null) return def;
        boolean[] m = new boolean[9];
        for (int n : counts) {
            if (n < 0 || n > 8) throw new InvalidParameterException("규칙의 이웃 수는 0~8 사이여야 합니다.");
            m[n] = true;
        }
        return m;
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
