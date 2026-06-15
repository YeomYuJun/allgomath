package com.yy.allgomath.voronoi;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.simulation.Computer;
import com.yy.allgomath.voronoi.dto.VoronoiParams;
import com.yy.allgomath.voronoi.dto.VoronoiResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 격자 최근접-사이트 Voronoi + Delaunay 듀얼(격자 인접 근사). one-shot. */
@Service
public class VoronoiService implements Computer<VoronoiParams, VoronoiResult> {

    private static final int MAX_SITES = 64;

    @Override
    public VoronoiResult compute(VoronoiParams params) {
        double[][] sites = params.sites();
        validate(sites, params.metric());
        int grid = params.grid();
        boolean manhattan = "manhattan".equals(params.metric());
        int[][] owner = new int[grid][grid];
        for (int gy = 0; gy < grid; gy++) {
            double y = (gy + 0.5) / grid;
            for (int gx = 0; gx < grid; gx++) {
                double x = (gx + 0.5) / grid;
                int best = 0;
                double bestD = Double.MAX_VALUE;
                for (int i = 0; i < sites.length; i++) {
                    double d = dist(x, y, sites[i][0], sites[i][1], manhattan);
                    if (d < bestD) { bestD = d; best = i; }
                }
                owner[gy][gx] = best;
            }
        }
        return new VoronoiResult(owner, grid, edges(owner, grid));
    }

    private static double dist(double ax, double ay, double bx, double by, boolean manhattan) {
        if (manhattan) return Math.abs(ax - bx) + Math.abs(ay - by);
        double dx = ax - bx, dy = ay - by;
        return dx * dx + dy * dy;
    }

    private static int[][] edges(int[][] owner, int grid) {
        Set<Long> seen = new LinkedHashSet<>();
        for (int gy = 0; gy < grid; gy++) {
            for (int gx = 0; gx < grid; gx++) {
                int o = owner[gy][gx];
                if (gx + 1 < grid) addEdge(seen, o, owner[gy][gx + 1]);
                if (gy + 1 < grid) addEdge(seen, o, owner[gy + 1][gx]);
            }
        }
        List<int[]> out = new ArrayList<>(seen.size());
        for (long key : seen) out.add(new int[]{(int) (key >> 20), (int) (key & 0xFFFFF)});
        return out.toArray(new int[0][]);
    }

    private static void addEdge(Set<Long> seen, int a, int b) {
        if (a == b) return;
        int lo = Math.min(a, b), hi = Math.max(a, b);
        seen.add(((long) lo << 20) | hi);
    }

    private void validate(double[][] sites, String metric) {
        if (sites == null || sites.length == 0 || sites.length > MAX_SITES) {
            throw new InvalidParameterException("사이트 수는 1~" + MAX_SITES + " 사이여야 합니다.");
        }
        for (double[] s : sites) {
            if (s == null || s.length != 2) {
                throw new InvalidParameterException("각 사이트는 [x, y] 형식이어야 합니다.");
            }
        }
        if (!"euclid".equals(metric) && !"manhattan".equals(metric)) {
            throw new InvalidParameterException("metric은 euclid 또는 manhattan이어야 합니다.");
        }
    }
}
