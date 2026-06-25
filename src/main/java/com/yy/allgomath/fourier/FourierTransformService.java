package com.yy.allgomath.fourier;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.fourier.dto.Bin;
import com.yy.allgomath.fourier.dto.FourierTransformParams;
import com.yy.allgomath.fourier.dto.FourierTransformResult;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 실수 DFT (O(N²)): 단측 진폭 스펙트럼과 피크 주파수를 one-shot으로 반환.
 * N <= 4096이므로 FFT 없이 직접 DFT.
 */
@Service
public class FourierTransformService implements Computer<FourierTransformParams, FourierTransformResult> {

    @Override
    public FourierTransformResult compute(FourierTransformParams params) {
        // 검증: compute 최초 진입 시점
        double[] signal = params.signal();
        if (signal == null || signal.length < 16 || signal.length > 4096) {
            throw new InvalidParameterException("signal 길이는 16 이상 4096 이하여야 합니다.");
        }
        if (params.sampleRate() <= 0) {
            throw new InvalidParameterException("sampleRate는 0보다 커야 합니다.");
        }

        int N = signal.length;
        int half = N / 2;
        Bin[] spectrum = new Bin[half + 1];

        for (int k = 0; k <= half; k++) {
            double re = 0.0, im = 0.0;
            for (int n = 0; n < N; n++) {
                double ang = 2.0 * Math.PI * k * n / N;
                re += signal[n] * Math.cos(ang);
                im -= signal[n] * Math.sin(ang);
            }
            double scale = (k == 0) ? 1.0 / N : 2.0 / N;
            double mag = Math.hypot(re, im) * scale;
            double freq = (double) k * params.sampleRate() / N;
            spectrum[k] = new Bin(freq, mag);
        }

        double[] peaks = findPeaks(spectrum);
        return new FourierTransformResult(spectrum, peaks);
    }

    private double[] findPeaks(Bin[] spectrum) {
        // globalMax
        double globalMax = 0.0;
        for (Bin b : spectrum) {
            if (b.mag() > globalMax) globalMax = b.mag();
        }
        double threshold = 0.45 * globalMax;

        List<Double> peakFreqs = new ArrayList<>();
        int i = 1;
        while (i < spectrum.length - 1) {
            double mag = spectrum[i].mag();
            if (mag > spectrum[i - 1].mag() && mag >= spectrum[i + 1].mag() && mag > threshold) {
                peakFreqs.add(spectrum[i].freq());
                // skip neighbourhood: advance past contiguous bins still above threshold
                int j = i + 1;
                while (j < spectrum.length && spectrum[j].mag() > threshold) {
                    j++;
                }
                i = j; // resume after the neighbourhood
            } else {
                i++;
            }
        }

        double[] result = new double[peakFreqs.size()];
        for (int k = 0; k < result.length; k++) result[k] = peakFreqs.get(k);
        return result;
    }
}
