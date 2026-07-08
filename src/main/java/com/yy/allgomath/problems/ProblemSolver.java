package com.yy.allgomath.problems;

import com.yy.allgomath.problems.dto.ProblemSolveRequest;
import com.yy.allgomath.problems.dto.TraceEnvelope;

/** 문제 플러그인 전략. @Component로 등록만 하면 problemId로 라우팅된다. */
public interface ProblemSolver {

    String problemId();

    TraceEnvelope solve(ProblemSolveRequest req);
}
