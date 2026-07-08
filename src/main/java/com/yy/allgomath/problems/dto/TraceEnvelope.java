package com.yy.allgomath.problems.dto;

import java.util.List;
import java.util.Map;

/** 문제 플러그인 공통 응답 포맷. meta는 요약 정보, frames는 순서 있는 원자 연산 목록. */
public record TraceEnvelope(Map<String, Object> meta, List<TraceFrame> frames) {
}
