package com.yy.allgomath.fourier;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.fourier.dto.FourierParams;
import com.yy.allgomath.fourier.dto.Harmonic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FourierServiceTest {

    private final FourierService svc = new FourierService();

    @Test
    void square_has_odd_harmonics_with_4_over_pi_n() {
        List<Harmonic> h = svc.compute(new FourierParams("square", 3)).harmonics();
        assertEquals(1, h.get(0).n());
        assertEquals(4.0 / Math.PI, h.get(0).a(), 1e-9);
        assertEquals(3, h.get(1).n());
        assertEquals(4.0 / (3 * Math.PI), h.get(1).a(), 1e-9);
        assertEquals(5, h.get(2).n());
    }

    @Test
    void saw_alternates_sign() {
        List<Harmonic> h = svc.compute(new FourierParams("saw", 2)).harmonics();
        assertEquals(2.0 / Math.PI, h.get(0).a(), 1e-9);
        assertEquals(-1.0 / Math.PI, h.get(1).a(), 1e-9);
    }

    @Test
    void triangle_has_odd_harmonics_alternating() {
        List<Harmonic> h = svc.compute(new FourierParams("triangle", 2)).harmonics();
        assertEquals(1, h.get(0).n());
        assertEquals(8.0 / (Math.PI * Math.PI), h.get(0).a(), 1e-9);
        assertEquals(3, h.get(1).n());
        assertTrue(h.get(1).a() < 0);
    }

    @Test
    void returns_n_harmonics() {
        assertEquals(8, svc.compute(new FourierParams("square", 8)).harmonics().size());
    }

    @Test
    void bad_wave_throws() {
        assertThrows(InvalidParameterException.class, () -> svc.compute(new FourierParams("noise", 4)));
    }
}
