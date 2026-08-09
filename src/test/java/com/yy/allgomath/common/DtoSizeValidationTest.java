package com.yy.allgomath.common;

import com.yy.allgomath.fourier.dto.FourierTransformRequest;
import com.yy.allgomath.sort.dto.SortRunRequest;
import com.yy.allgomath.voronoi.dto.VoronoiComputeRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoSizeValidationTest {

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

    @Test
    void oversizedSortValues_violates() {
        assertFalse(validator.validate(new SortRunRequest("bubble", new int[200])).isEmpty());
    }

    @Test
    void withinLimitSortValues_ok() {
        assertTrue(validator.validate(new SortRunRequest("bubble", new int[64])).isEmpty());
    }

    @Test
    void oversizedSignal_violates() {
        assertFalse(validator.validate(new FourierTransformRequest(new double[5000], 4096)).isEmpty());
    }

    @Test
    void oversizedVoronoiSites_violates() {
        assertFalse(validator.validate(new VoronoiComputeRequest(new double[100][2], "euclid", 160)).isEmpty());
    }
}
