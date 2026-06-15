package com.yy.allgomath.plotter;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.plotter.dto.DescentParams;
import com.yy.allgomath.plotter.dto.DescentResult;
import com.yy.allgomath.plotter.dto.SurfaceParams;
import com.yy.allgomath.plotter.dto.SurfaceResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlotterServiceTest {

    private final PlotterService svc = new PlotterService();

    @Test
    void surface_grid_dimensions_and_extent() {
        SurfaceResult r = svc.surface(new SurfaceParams("bowl", 2.0, 10));
        assertEquals(11, r.z().length);
        assertEquals(11, r.z()[0].length);
        assertTrue(r.zMin() <= r.zMax());
        assertEquals(0.0, r.critical().x(), 1e-9);
        assertEquals(0.0, r.critical().y(), 1e-9);
    }

    @Test
    void surface_bowl_corner_value() {
        SurfaceResult r = svc.surface(new SurfaceParams("bowl", 2.0, 10));
        assertEquals(8.0, r.z()[0][0], 1e-9); // corner (-2,-2): 4+4
    }

    @Test
    void function_gradients_are_analytic() {
        assertArrayEquals(new double[]{2, 2}, SurfaceFunction.of("bowl").grad(1, 1), 1e-9);
        assertArrayEquals(new double[]{2, -2}, SurfaceFunction.of("saddle").grad(1, 1), 1e-9);
        assertArrayEquals(new double[]{9, -12}, SurfaceFunction.of("monkey").grad(2, 1), 1e-9); // [3*4-3*1, -6*2*1]
    }

    @Test
    void rosenbrock_critical_is_one_one() {
        assertArrayEquals(new double[]{1, 1}, SurfaceFunction.of("rosenbrock").critical(), 1e-9);
    }

    @Test
    void descent_on_bowl_converges_to_origin() {
        DescentResult r = svc.descend(new DescentParams("bowl", 2.0, 2.0, 0.1, 200));
        assertTrue(r.converged());
        var last = r.path().get(r.path().size() - 1);
        assertEquals(0.0, last.x(), 1e-3);
        assertEquals(0.0, last.y(), 1e-3);
    }

    @Test
    void descent_path_is_monotonic_non_increasing() {
        DescentResult r = svc.descend(new DescentParams("bowl", 2.0, 2.0, 0.1, 50));
        var p = r.path();
        assertEquals(2.0, p.get(0).x(), 1e-9);
        for (int i = 1; i < p.size(); i++) {
            assertTrue(p.get(i).z() <= p.get(i - 1).z() + 1e-9);
        }
    }

    @Test
    void unknown_function_throws() {
        assertThrows(InvalidParameterException.class, () -> svc.surface(new SurfaceParams("nope", 2.0, 10)));
    }

    @Test
    void out_of_range_throws() {
        assertThrows(InvalidParameterException.class, () -> svc.surface(new SurfaceParams("bowl", 99.0, 10)));
    }
}
