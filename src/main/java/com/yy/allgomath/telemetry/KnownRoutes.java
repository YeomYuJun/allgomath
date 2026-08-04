package com.yy.allgomath.telemetry;

import java.util.Set;

/** 텔레메트리가 라벨로 허용하는 라우트 패턴. 목록 밖 값은 카디널리티 방어를 위해 버린다. */
public final class KnownRoutes {

    private static final Set<String> PATTERNS = Set.of(
            "/",
            "/fractal",
            "/monte-carlo",
            "/bezier",
            "/cellular-automata",
            "/double-pendulum",
            "/lissajous",
            "/flow",
            "/voronoi",
            "/fourier",
            "/plotter",
            "/breadth-first-search",
            "/dynamic-programming",
            "/depth-first-search",
            "/greedy",
            "/fourier-transform",
            "/sorting",
            "/problems/:problemId",
            "/privacy"
    );

    private KnownRoutes() {
    }

    public static boolean contains(String route) {
        return route != null && PATTERNS.contains(route);
    }

    public static int size() {
        return PATTERNS.size();
    }
}
