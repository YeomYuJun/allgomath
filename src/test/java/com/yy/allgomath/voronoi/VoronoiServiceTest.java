package com.yy.allgomath.voronoi;

import com.yy.allgomath.voronoi.dto.VoronoiParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoronoiServiceTest {

    private final VoronoiService svc = new VoronoiService();

    @Test
    void left_and_right_cells_owned_by_nearest_site() {
        var r = svc.compute(new VoronoiParams(new double[][]{{0.2, 0.5}, {0.8, 0.5}}, "euclid", 10));
        assertEquals(0, r.owner()[5][1]);
        assertEquals(1, r.owner()[5][8]);
    }

    @Test
    void single_site_owns_everything_no_edges() {
        var r = svc.compute(new VoronoiParams(new double[][]{{0.5, 0.5}}, "euclid", 8));
        assertEquals(0, r.owner()[0][0]);
        assertEquals(0, r.owner()[7][7]);
        assertEquals(0, r.edges().length);
    }

    @Test
    void two_sites_produce_edge_0_1() {
        var r = svc.compute(new VoronoiParams(new double[][]{{0.2, 0.5}, {0.8, 0.5}}, "euclid", 10));
        assertTrue(r.edges().length >= 1);
        assertEquals(0, r.edges()[0][0]);
        assertEquals(1, r.edges()[0][1]);
    }

    @Test
    void owner_is_grid_by_grid() {
        var r = svc.compute(new VoronoiParams(new double[][]{{0.3, 0.3}, {0.7, 0.7}}, "euclid", 32));
        assertEquals(32, r.owner().length);
        assertEquals(32, r.owner()[0].length);
        assertEquals(32, r.grid());
    }

    @Test
    void deterministic_for_same_input() {
        var a = svc.compute(new VoronoiParams(new double[][]{{0.3, 0.3}, {0.7, 0.7}, {0.5, 0.9}}, "manhattan", 20));
        var b = svc.compute(new VoronoiParams(new double[][]{{0.3, 0.3}, {0.7, 0.7}, {0.5, 0.9}}, "manhattan", 20));
        assertArrayEquals(a.owner()[10], b.owner()[10]);
    }
}
