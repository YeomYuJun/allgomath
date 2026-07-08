package com.yy.allgomath.problems;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.problems.dto.ProblemSolveRequest;
import com.yy.allgomath.problems.dto.TraceEnvelope;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 문제 플러그인 공통 API. problemId로 등록된 {@link ProblemSolver}에 위임한다. */
@RestController
@RequestMapping("/api/algorithms/problems")
public class ProblemsController {

    private final Map<String, ProblemSolver> solvers;

    public ProblemsController(List<ProblemSolver> registered) {
        this.solvers = registered.stream()
                .collect(Collectors.toMap(ProblemSolver::problemId, Function.identity()));
    }

    @PostMapping("/solve")
    public ResponseEntity<TraceEnvelope> solve(@Valid @RequestBody ProblemSolveRequest req) {
        ProblemSolver solver = solvers.get(req.problemId());
        if (solver == null) {
            throw new InvalidParameterException("지원하지 않는 problemId입니다: " + req.problemId());
        }
        return ResponseEntity.ok(solver.solve(req));
    }
}
