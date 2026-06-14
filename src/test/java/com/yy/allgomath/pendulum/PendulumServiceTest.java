package com.yy.allgomath.pendulum;

import com.yy.allgomath.pendulum.dto.Bob;
import com.yy.allgomath.pendulum.dto.PendulumParams;
import com.yy.allgomath.pendulum.dto.PendulumState;
import com.yy.allgomath.simulation.SimulationResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PendulumServiceTest {

    private final PendulumService svc = new PendulumService();

    private PendulumParams params(double t1, double t2, double damping) {
        Bob a = new Bob(t1, t2, 0, 0);
        Bob b = new Bob(t1 + 0.012, t2, 0, 0);
        return new PendulumParams(new PendulumState(a, b), 1.2, 1.0, damping);
    }

    /** 총 에너지(m1=m2=1, L1=1, L2=ratio). 테스트 오라클. */
    private double energy(Bob b, PendulumParams p) {
        double t1 = b.t1(), t2 = b.t2(), w1 = b.w1(), w2 = b.w2();
        double L2 = p.armRatio(), g = p.gravity();
        double v2sq = w1 * w1 + L2 * L2 * w2 * w2 + 2 * L2 * w1 * w2 * Math.cos(t1 - t2);
        double ke = 0.5 * (w1 * w1) + 0.5 * v2sq;
        double pe = -2 * g * Math.cos(t1) - g * L2 * Math.cos(t2);
        return ke + pe;
    }

    @Test
    void rest_stays_at_rest() {
        PendulumParams p = new PendulumParams(
                new PendulumState(new Bob(0, 0, 0, 0), new Bob(0, 0, 0, 0)), 1.2, 1.0, 0.0);
        Bob last = svc.simulate(p, 20).steps().get(19).a();
        assertEquals(0.0, last.t1(), 1e-9);
        assertEquals(0.0, last.t2(), 1e-9);
    }

    @Test
    void energy_is_conserved_without_damping() {
        PendulumParams p = params(1.0, 1.0, 0.0);
        double e0 = energy(p.state().a(), p);
        Bob last = svc.simulate(p, 30).steps().get(29).a();
        assertEquals(e0, energy(last, p), 0.05);
    }

    @Test
    void twin_diverges_over_time() {
        PendulumParams p = params(1.954, 2.670, 0.0); // 112°, 153°
        SimulationResponse<PendulumState> r = svc.simulate(p, 120);
        double first = r.series()[0];
        double last = r.series()[119];
        assertTrue(first < 0.1, "초기 발산도 작음");
        assertTrue(last > first, "시간이 지나며 발산 증가");
        assertTrue(last > 0.2, "충분히 갈라짐");
    }

    @Test
    void deterministic_for_same_input() {
        Bob a1 = svc.simulate(params(1.5, 2.0, 0.0), 50).steps().get(49).a();
        Bob a2 = svc.simulate(params(1.5, 2.0, 0.0), 50).steps().get(49).a();
        assertEquals(a1.t1(), a2.t1(), 0.0);
        assertEquals(a1.t2(), a2.t2(), 0.0);
    }

    @Test
    void returns_requested_sizes() {
        SimulationResponse<PendulumState> r = svc.simulate(params(1.0, 1.0, 0.0), 10);
        assertEquals(10, r.steps().size());
        assertEquals(10, r.series().length);
    }
}
