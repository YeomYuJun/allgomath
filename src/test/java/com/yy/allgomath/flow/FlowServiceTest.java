package com.yy.allgomath.flow;

import com.yy.allgomath.flow.dto.FlowParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlowServiceTest {

    private final FlowService svc = new FlowService();

    @Test
    void noise_in_unit_range_and_deterministic() {
        double n1 = FlowService.noise(1.3, 2.7);
        assertEquals(n1, FlowService.noise(1.3, 2.7), 0.0);
        assertTrue(n1 >= 0 && n1 <= 1);
    }

    @Test
    void noise_is_continuous() {
        assertTrue(Math.abs(FlowService.noise(1.0, 1.0) - FlowService.noise(1.001, 1.0)) < 0.01);
    }

    @Test
    void particle_moves_by_step_distance() {
        double[][] particles = {{50, 50}};
        double[] p = svc.simulate(new FlowParams(particles, 1.0, 0.0), 1).steps().get(0)[0];
        assertEquals(0.6, Math.hypot(p[0] - 50, p[1] - 50), 1e-9);
    }

    @Test
    void returns_requested_sizes() {
        double[][] particles = {{10, 10}, {20, 20}, {30, 30}};
        var r = svc.simulate(new FlowParams(particles, 1.4, 0.0), 25);
        assertEquals(25, r.steps().size());
        assertEquals(25, r.series().length);
        assertEquals(3, r.steps().get(0).length);
    }

    @Test
    void deterministic_for_same_input() {
        double a = svc.simulate(new FlowParams(new double[][]{{33, 44}}, 1.4, 0.5), 20).steps().get(19)[0][0];
        double b = svc.simulate(new FlowParams(new double[][]{{33, 44}}, 1.4, 0.5), 20).steps().get(19)[0][0];
        assertEquals(a, b, 0.0);
    }
}
