package com.yy.allgomath.lissajous;

import com.yy.allgomath.lissajous.dto.LissajousParams;
import com.yy.allgomath.lissajous.dto.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LissajousServiceTest {

    private final LissajousService svc = new LissajousService();
    private static final double DU = 2 * Math.PI / 300;

    @Test
    void first_point_is_on_curve() {
        Point p = svc.simulate(new LissajousParams(3, 2, 0.0, 0.0), 1).steps().get(0);
        assertEquals(Math.sin(3 * DU), p.x(), 1e-12);
        assertEquals(Math.sin(2 * DU), p.y(), 1e-12);
    }

    @Test
    void closes_after_one_period() {
        double delta = Math.PI / 2;
        Point last = svc.simulate(new LissajousParams(3, 2, delta, 0.0), 300).steps().get(299);
        assertEquals(Math.sin(delta), last.x(), 1e-3);
        assertEquals(0.0, last.y(), 1e-3);
    }

    @Test
    void returns_requested_sizes() {
        var r = svc.simulate(new LissajousParams(5, 4, 1.0, 0.0), 50);
        assertEquals(50, r.steps().size());
        assertEquals(50, r.series().length);
    }

    @Test
    void deterministic_for_same_input() {
        Point a = svc.simulate(new LissajousParams(3, 2, 1.0, 0.5), 100).steps().get(99);
        Point b = svc.simulate(new LissajousParams(3, 2, 1.0, 0.5), 100).steps().get(99);
        assertEquals(a.x(), b.x(), 0.0);
        assertEquals(a.y(), b.y(), 0.0);
    }
}
