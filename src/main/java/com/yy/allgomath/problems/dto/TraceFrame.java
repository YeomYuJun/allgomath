package com.yy.allgomath.problems.dto;

import java.util.Map;

/** 표준 trace envelope의 원자 연산 1개. FE는 op별 렌더만 구현하면 재생은 공통. */
public record TraceFrame(String op, Map<String, Object> args, String note) {

    public static TraceFrame of(String op, Map<String, Object> args) {
        return new TraceFrame(op, args, null);
    }
}
