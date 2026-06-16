package com.yy.allgomath.montecarlo;

import com.yy.allgomath.montecarlo.dto.MonteCarloRequest;
import com.yy.allgomath.montecarlo.dto.MonteCarloResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonteCarloServiceTest {

    private final MonteCarloService service = new MonteCarloService();

    @Test
    void unitCircleIntegrationApproximatesPi() {
        MonteCarloRequest req = new MonteCarloRequest(
                200_000,
                new MonteCarloRequest.Bounds(-1.0, 1.0, -1.0, 1.0),
                "unit_circle");

        MonteCarloResult result = service.performMonteCarloIntegration(req);

        assertEquals(200_000, result.getTotalCount());
        assertEquals(200_000, result.getPoints().size());
        assertEquals(Math.PI, result.getActualValue(), 1e-9);
        assertEquals(Math.PI, result.getEstimate(), 0.15);
    }

    @Test
    void resultIsWellFormed() {
        MonteCarloRequest req = new MonteCarloRequest(
                50_000,
                new MonteCarloRequest.Bounds(-1.0, 1.0, -1.0, 1.0),
                "unit_circle");

        MonteCarloResult result = service.performMonteCarloIntegration(req);

        assertTrue(result.getInsideCount() >= 0 && result.getInsideCount() <= result.getTotalCount());
        assertTrue(result.getEstimate() >= 0.0 && result.getEstimate() <= 4.0);
        assertTrue(result.getConvergenceHistory().size() > 0);
    }
}
