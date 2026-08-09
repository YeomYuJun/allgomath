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
    void frames_capped_and_series_lockstep_beyond_limit() {
        double[][] particles = {{10, 10}, {20, 20}};
        var r = svc.simulate(new FlowParams(particles, 1.4, 0.0), 200);
        assertTrue(r.steps().size() <= 60, "프레임은 상한 이하");
        assertEquals(r.steps().size(), r.series().length, "series는 프레임과 lockstep");
    }

    @Test
    void capped_preserves_final_state() {
        var frames = svc.simulate(new FlowParams(new double[][]{{33, 44}}, 1.4, 0.0), 200).steps();
        double capped = frames.get(frames.size() - 1)[0][0];
        // 마지막 프레임은 200 step 전진한 최종 상태여야 한다(캡 없이 200번째와 동일)
        double full = fullRun(new double[][]{{33, 44}}, 1.4, 0.0, 200);
        assertEquals(full, capped, 1e-9);
    }

    private double fullRun(double[][] particles, double scale, double time, int steps) {
        double t = time;
        double[] p = particles[0];
        for (int i = 0; i < steps; i++) {
            double a = FlowService.noise(p[0] * 0.03 * scale, p[1] * 0.03 * scale) * 2 * Math.PI + t;
            p[0] += Math.cos(a) * 0.6;
            p[1] += Math.sin(a) * 0.6;
            t += 0.01;
        }
        return p[0];
    }

    @Test
    void deterministic_for_same_input() {
        double a = svc.simulate(new FlowParams(new double[][]{{33, 44}}, 1.4, 0.5), 20).steps().get(19)[0][0];
        double b = svc.simulate(new FlowParams(new double[][]{{33, 44}}, 1.4, 0.5), 20).steps().get(19)[0][0];
        assertEquals(a, b, 0.0);
    }
}
