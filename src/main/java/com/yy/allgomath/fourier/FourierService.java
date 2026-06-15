package com.yy.allgomath.fourier;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.fourier.dto.FourierParams;
import com.yy.allgomath.fourier.dto.FourierResult;
import com.yy.allgomath.fourier.dto.Harmonic;
import com.yy.allgomath.simulation.Computer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 파형별 닫힌형 Fourier 사인-급수 계수. one-shot. */
@Service
public class FourierService implements Computer<FourierParams, FourierResult> {

    @Override
    public FourierResult compute(FourierParams params) {
        int count = params.N();
        List<Harmonic> harmonics = new ArrayList<>(count);
        switch (params.wave()) {
            case "square" -> {
                for (int k = 0; k < count; k++) {
                    int n = 2 * k + 1;
                    harmonics.add(new Harmonic(n, 4.0 / (Math.PI * n)));
                }
            }
            case "saw" -> {
                for (int n = 1; n <= count; n++) {
                    harmonics.add(new Harmonic(n, (2.0 / (Math.PI * n)) * (n % 2 == 1 ? 1 : -1)));
                }
            }
            case "triangle" -> {
                for (int k = 0; k < count; k++) {
                    int n = 2 * k + 1;
                    harmonics.add(new Harmonic(n, (8.0 / (Math.PI * Math.PI * n * n)) * (k % 2 == 0 ? 1 : -1)));
                }
            }
            default -> throw new InvalidParameterException("wave는 square, saw, triangle 중 하나여야 합니다.");
        }
        return new FourierResult(harmonics);
    }
}
