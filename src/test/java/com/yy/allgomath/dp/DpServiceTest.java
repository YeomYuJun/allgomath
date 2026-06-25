package com.yy.allgomath.dp;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.dp.dto.DpParams;
import com.yy.allgomath.dp.dto.DpResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DpServiceTest {

    private final DpService svc = new DpService();

    private static final int[][] GRID_2X2 = {{1, 2}, {3, 4}};

    @Test
    void max_2x2_best_and_dp_table() {
        DpResult r = svc.compute(new DpParams(GRID_2X2, "max"));
        assertEquals(8, r.best());
        assertArrayEquals(new int[]{1, 3}, r.dp()[0]);
        assertArrayEquals(new int[]{4, 8}, r.dp()[1]);
    }

    @Test
    void max_2x2_from_table() {
        DpResult r = svc.compute(new DpParams(GRID_2X2, "max"));
        assertNull(r.from()[0][0]);
        assertEquals("left", r.from()[0][1]);
        assertEquals("up",   r.from()[1][0]);
        assertEquals("left", r.from()[1][1]);
    }

    @Test
    void max_2x2_path() {
        DpResult r = svc.compute(new DpParams(GRID_2X2, "max"));
        int[][] path = r.path();
        assertEquals(3, path.length);
        assertArrayEquals(new int[]{0, 0}, path[0]);
        assertArrayEquals(new int[]{1, 0}, path[1]);
        assertArrayEquals(new int[]{1, 1}, path[2]);
    }

    @Test
    void max_2x2_fillOrder() {
        DpResult r = svc.compute(new DpParams(GRID_2X2, "max"));
        assertEquals(4, r.fillOrder().length);
        assertArrayEquals(new int[]{0, 0}, r.fillOrder()[0]);
    }

    @Test
    void min_2x2_best_and_path() {
        DpResult r = svc.compute(new DpParams(GRID_2X2, "min"));
        assertEquals(7, r.best());
        int[][] path = r.path();
        assertEquals(3, path.length);
        assertArrayEquals(new int[]{0, 0}, path[0]);
        assertArrayEquals(new int[]{0, 1}, path[1]);
        assertArrayEquals(new int[]{1, 1}, path[2]);
    }

    @Test
    void ragged_grid_throws() {
        int[][] ragged = {{1, 2}, {3}};
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new DpParams(ragged, "max")));
    }

    @Test
    void grid_too_small_throws() {
        int[][] oneByOne = {{5}};
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new DpParams(oneByOne, "max")));
    }

    @Test
    void zero_value_throws() {
        int[][] withZero = {{1, 2}, {0, 4}};
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new DpParams(withZero, "max")));
    }

    @Test
    void invalid_mode_throws() {
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new DpParams(GRID_2X2, "foo")));
    }
}
