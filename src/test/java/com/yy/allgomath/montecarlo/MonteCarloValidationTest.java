package com.yy.allgomath.montecarlo;

import com.yy.allgomath.montecarlo.dto.MonteCarloRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonteCarloValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void teardown() {
        factory.close();
    }

    private MonteCarloRequest req(int iterations, String fn) {
        return new MonteCarloRequest(iterations, new MonteCarloRequest.Bounds(-1, 1, -1, 1), fn);
    }

    @Test
    void oversizedIterations_violates() {
        assertFalse(validator.validate(req(50_000_000, "ellipse")).isEmpty());
    }

    @Test
    void zeroIterations_violates() {
        assertFalse(validator.validate(req(0, "ellipse")).isEmpty());
    }

    @Test
    void missingFunctionType_violates() {
        assertFalse(validator.validate(req(3000, null)).isEmpty());
    }

    @Test
    void missingBounds_violates() {
        assertFalse(validator.validate(new MonteCarloRequest(3000, null, "ellipse")).isEmpty());
    }

    @Test
    void validRequest_ok() {
        assertTrue(validator.validate(req(3000, "ellipse")).isEmpty());
    }
}
