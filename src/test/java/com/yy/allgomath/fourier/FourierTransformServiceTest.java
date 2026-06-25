package com.yy.allgomath.fourier;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.fourier.dto.FourierTransformParams;
import com.yy.allgomath.fourier.dto.FourierTransformResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FourierTransformServiceTest {

    private final FourierTransformService svc = new FourierTransformService();

    @Test
    void single_cosine_yields_one_peak_at_correct_frequency() {
        int N = 64;
        double sampleRate = 64.0;
        double[] signal = new double[N];
        for (int n = 0; n < N; n++) {
            signal[n] = Math.cos(2 * Math.PI * 8 * n / N);
        }
        FourierTransformResult result = svc.compute(new FourierTransformParams(signal, sampleRate));

        assertEquals(33, result.spectrum().length);
        assertEquals(1, result.peaks().length);
        assertEquals(8.0, result.peaks()[0], 1.0);
    }

    @Test
    void two_cosines_yield_two_peaks_at_correct_frequencies() {
        int N = 64;
        double sampleRate = 64.0;
        double[] signal = new double[N];
        for (int n = 0; n < N; n++) {
            signal[n] = Math.cos(2 * Math.PI * 5 * n / N) + Math.cos(2 * Math.PI * 12 * n / N);
        }
        FourierTransformResult result = svc.compute(new FourierTransformParams(signal, sampleRate));

        assertEquals(2, result.peaks().length);
        boolean foundFive = false, foundTwelve = false;
        for (double peak : result.peaks()) {
            if (Math.abs(peak - 5.0) < 1.0) foundFive = true;
            if (Math.abs(peak - 12.0) < 1.0) foundTwelve = true;
        }
        assertTrue(foundFive, "peak near 5 Hz not found");
        assertTrue(foundTwelve, "peak near 12 Hz not found");
    }

    @Test
    void signal_too_short_throws() {
        double[] signal = new double[8];
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new FourierTransformParams(signal, 64.0)));
    }

    @Test
    void zero_sample_rate_throws() {
        double[] signal = new double[64];
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new FourierTransformParams(signal, 0.0)));
    }
}
