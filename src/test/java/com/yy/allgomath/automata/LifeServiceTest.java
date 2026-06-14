package com.yy.allgomath.automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LifeServiceTest {

    private final LifeService svc = new LifeService();

    /** 'X'=alive 문자열 행들로 그리드 생성 */
    private boolean[][] grid(String... rows) {
        boolean[][] g = new boolean[rows.length][rows[0].length()];
        for (int r = 0; r < rows.length; r++)
            for (int c = 0; c < rows[r].length(); c++)
                g[r][c] = rows[r].charAt(c) == 'X';
        return g;
    }

    @Test
    void blinker_oscillates_with_period_2() {
        boolean[][] horizontal = grid(
                ".....",
                ".....",
                ".XXX.",
                ".....",
                ".....");
        boolean[][] vertical = svc.nextGeneration(horizontal);
        assertTrue(vertical[1][2] && vertical[2][2] && vertical[3][2], "수직으로 전환");
        assertFalse(vertical[2][1] || vertical[2][3], "가로 끝은 죽음");
        boolean[][] back = svc.nextGeneration(vertical);
        assertArrayEquals(horizontal, back, "주기 2로 복귀");
    }

    @Test
    void block_is_stable() {
        boolean[][] block = grid(
                "....",
                ".XX.",
                ".XX.",
                "....");
        assertArrayEquals(block, svc.nextGeneration(block), "블록은 정지 상태");
    }

    @Test
    void glider_moves_by_one_diagonally_after_4_generations() {
        boolean[][] g = grid(
                "........",
                ".X......",
                "..X.....",
                "XXX.....",
                "........",
                "........",
                "........",
                "........");
        boolean[][] cur = g;
        for (int i = 0; i < 4; i++) cur = svc.nextGeneration(cur);
        // 원본 글라이더가 (1,1) 만큼 이동
        boolean[][] expected = grid(
                "........",
                "........",
                "..X.....",
                "...X....",
                ".XXX....",
                "........",
                "........",
                "........");
        assertArrayEquals(expected, cur, "4세대 후 대각 1칸 이동");
    }

    @Test
    void simulate_returns_requested_number_of_generations() {
        boolean[][] g = grid(".....", ".XXX.", ".....");
        var resp = svc.simulate(g, 3);
        assertEquals(3, resp.steps().size());
        assertEquals(3, resp.series().length);
    }

    @Test
    void population_counts_alive_cells() {
        assertEquals(3, svc.population(grid(".XXX.")));
    }
}
