package com.yy.allgomath.bezier;

import com.yy.allgomath.bezier.dto.BezierParams;
import com.yy.allgomath.common.exception.InvalidParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierServiceTest {

    private final BezierService svc = new BezierService();

    @Test
    void linear_curve_samples_endpoints_and_midpoint() {
        var r = svc.compute(new BezierParams(new double[][]{{0, 0}, {1, 1}}, 4));
        assertArrayEquals(new double[]{0, 0}, r.curve()[0], 1e-9);
        assertArrayEquals(new double[]{1, 1}, r.curve()[4], 1e-9);
        assertArrayEquals(new double[]{0.5, 0.5}, r.curve()[2], 1e-9);
    }

    @Test
    void quadratic_midpoint_matches_de_casteljau() {
        var r = svc.compute(new BezierParams(new double[][]{{0, 0}, {0.5, 1}, {1, 0}}, 2));
        assertArrayEquals(new double[]{0.5, 0.5}, r.curve()[1], 1e-9);
    }

    @Test
    void endpoints_interpolate_control_points() {
        double[][] cp = {{0.1, 0.8}, {0.3, 0.2}, {0.7, 0.9}, {0.9, 0.3}};
        var r = svc.compute(new BezierParams(cp, 20));
        assertArrayEquals(cp[0], r.curve()[0], 1e-9);
        assertArrayEquals(cp[3], r.curve()[r.curve().length - 1], 1e-9);
    }

    @Test
    void curve_length_is_samples_plus_one_and_degree_is_n_minus_one() {
        var r = svc.compute(new BezierParams(new double[][]{{0, 0}, {0.5, 1}, {1, 0}}, 50));
        assertEquals(51, r.curve().length);
        assertEquals(2, r.degree());
    }

    @Test
    void too_few_points_throws() {
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new BezierParams(new double[][]{{0, 0}}, 10)));
    }

    @Test
    void malformed_point_throws() {
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new BezierParams(new double[][]{{0, 0}, {1}}, 10)));
    }
}
