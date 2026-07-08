package com.yy.allgomath.problems.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/** 문제 플러그인 공통 요청. params는 설정값, input은 사용자가 편집하는 문제 데이터. */
public record ProblemSolveRequest(
        @NotBlank String problemId,
        Map<String, Object> params,
        Map<String, Object> input) {

    public Map<String, Object> paramsOrEmpty() {
        return params == null ? Map.of() : params;
    }

    public Map<String, Object> inputOrEmpty() {
        return input == null ? Map.of() : input;
    }
}
