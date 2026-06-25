package com.yy.allgomath.bfs;

import com.yy.allgomath.bfs.dto.BfsParams;
import com.yy.allgomath.bfs.dto.BfsResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BfsServiceTest {

    private final BfsService svc = new BfsService();

    private static boolean[] open(int n) {
        return new boolean[n]; // all false
    }

    @Test
    void open_4x4_shortest_path_corner_to_corner() {
        // rows=cols=4, start=(0,0)=0, goal=(3,3)=15, 4-neighbour
        BfsResult r = svc.compute(new BfsParams(4, 4, open(16), 0, 15, false));
        assertTrue(r.found());
        assertEquals(0, r.dist()[0]);
        assertEquals(6, r.dist()[15]);          // 3 down + 3 right
        assertEquals(7, r.path().length);       // 6 steps => 7 cells
        assertEquals(0, r.path()[0]);
        assertEquals(15, r.path()[r.path().length - 1]);
        assertEquals(0, r.order()[0]);          // start dequeued first
    }

    @Test
    void walled_off_goal_is_unreachable() {
        // goal=(3,3)=15 corner; its only in-bounds 4-neighbours are 11 and 14 - wall both
        boolean[] walls = open(16);
        walls[11] = true; // (2,3)
        walls[14] = true; // (3,2)
        BfsResult r = svc.compute(new BfsParams(4, 4, walls, 0, 15, false));
        assertFalse(r.found());
        assertEquals(0, r.path().length);
        assertEquals(-1, r.dist()[15]);
    }

    @Test
    void order_starts_at_start_and_contains_no_walls() {
        boolean[] walls = open(16);
        walls[5] = true;
        BfsResult r = svc.compute(new BfsParams(4, 4, walls, 0, 15, false));
        assertEquals(0, r.order()[0]);
        for (int cell : r.order()) assertFalse(walls[cell], "visited a wall cell: " + cell);
    }

    @Test
    void deterministic_for_same_input() {
        boolean[] walls = open(16);
        walls[6] = true;
        BfsParams p = new BfsParams(4, 4, walls, 0, 15, false);
        BfsResult a = svc.compute(p);
        BfsResult b = svc.compute(p);
        assertArrayEquals(a.order(), b.order());
        assertArrayEquals(a.path(), b.path());
    }

    @Test
    void diagonal_movement_shortens_corner_distance() {
        // with 8-neighbour, (0,0)->(3,3) is 3 diagonal steps (Chebyshev distance)
        BfsResult r = svc.compute(new BfsParams(4, 4, open(16), 0, 15, true));
        assertTrue(r.found());
        assertEquals(3, r.dist()[15]);
        assertEquals(4, r.path().length);
    }

    @org.junit.jupiter.api.Test
    void walls_length_must_match_grid() {
        org.junit.jupiter.api.Assertions.assertThrows(
            com.yy.allgomath.common.exception.InvalidParameterException.class,
            () -> svc.compute(new BfsParams(4, 4, open(15), 0, 14, false)));
    }

    @org.junit.jupiter.api.Test
    void start_on_wall_is_rejected() {
        boolean[] walls = open(16);
        walls[0] = true;
        org.junit.jupiter.api.Assertions.assertThrows(
            com.yy.allgomath.common.exception.InvalidParameterException.class,
            () -> svc.compute(new BfsParams(4, 4, walls, 0, 15, false)));
    }

    @org.junit.jupiter.api.Test
    void start_out_of_range_is_rejected() {
        org.junit.jupiter.api.Assertions.assertThrows(
            com.yy.allgomath.common.exception.InvalidParameterException.class,
            () -> svc.compute(new BfsParams(4, 4, open(16), -1, 15, false)));
    }
}
