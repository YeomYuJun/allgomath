package com.yy.allgomath.dfs;

import com.yy.allgomath.dfs.dto.DfsParams;
import com.yy.allgomath.dfs.dto.DfsResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DfsServiceTest {

    private final DfsService svc = new DfsService();

    private static boolean[] open(int n) {
        return new boolean[n]; // all false
    }

    @Test
    void open_4x4_finds_goal_corner_to_corner() {
        DfsResult r = svc.compute(new DfsParams(4, 4, open(16), 0, 15));
        assertTrue(r.found());
        assertEquals(0, r.path()[0]);
        assertEquals(15, r.path()[r.path().length - 1]);
        assertTrue(r.events().length > 0);
        assertEquals("push", r.events()[0].type());
        assertEquals(0, r.events()[0].cell());
    }

    @Test
    void walled_off_goal_is_unreachable() {
        // goal=15 corner in 4x4; wall its only in-bounds 4-neighbours: 11 (2,3) and 14 (3,2)
        boolean[] walls = open(16);
        walls[11] = true;
        walls[14] = true;
        DfsResult r = svc.compute(new DfsParams(4, 4, walls, 0, 15));
        assertFalse(r.found());
        assertEquals(0, r.path().length);
        assertTrue(r.deadEnds() > 0);
    }

    @Test
    void deterministic_for_same_input() {
        boolean[] walls = open(16);
        walls[6] = true;
        DfsParams p = new DfsParams(4, 4, walls, 0, 15);
        DfsResult a = svc.compute(p);
        DfsResult b = svc.compute(p);
        assertEquals(a.events().length, b.events().length);
        assertArrayEquals(a.path(), b.path());
    }

    @Test
    void walls_length_must_match_grid() {
        assertThrows(
            com.yy.allgomath.common.exception.InvalidParameterException.class,
            () -> svc.compute(new DfsParams(4, 4, open(15), 0, 14)));
    }

    @Test
    void start_on_wall_is_rejected() {
        boolean[] walls = open(16);
        walls[0] = true;
        assertThrows(
            com.yy.allgomath.common.exception.InvalidParameterException.class,
            () -> svc.compute(new DfsParams(4, 4, walls, 0, 15)));
    }

    @Test
    void start_out_of_range_is_rejected() {
        assertThrows(
            com.yy.allgomath.common.exception.InvalidParameterException.class,
            () -> svc.compute(new DfsParams(4, 4, open(16), -1, 15)));
    }
}
