package com.yy.allgomath.service;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.datatype.FFTStep;
import com.yy.allgomath.datatype.FrequencyDomainResult;
import com.yy.allgomath.datatype.TimeDomainSignal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FFTService {

    /**
     * 시간 도메인 신호에 FFT를 적용하여 주파수 도메인 결과를 계산합니다.
     */
    public FrequencyDomainResult computeFFT(TimeDomainSignal signal) {
        // 신호 데이터를 2의 거듭제곱 크기로 패딩
        int n = findNextPowerOfTwo(signal.getRealValues().length);
        Complex[] x = new Complex[n];

        // 실수 신호를 복소수 배열로 변환
        for (int i = 0; i < signal.getRealValues().length; i++) {
            x[i] = new Complex(signal.getRealValues()[i], 0);
        }
        for (int i = signal.getRealValues().length; i < n; i++) {
            x[i] = new Complex(0, 0);
        }

        // FFT 계산
        Complex[] result = fft(x);

        // 결과를 주파수, 진폭, 위상으로 변환
        double[] frequencies = new double[n/2];
        double[] magnitudes = new double[n/2];
        double[] phases = new double[n/2];

        for (int i = 0; i < n/2; i++) {
            frequencies[i] = i * signal.getSamplingRate() / n;
            magnitudes[i] = result[i].magnitude();
            phases[i] = result[i].phase();
        }

        return new FrequencyDomainResult(frequencies, magnitudes, phases);
    }

    /**
     * 주파수 도메인 데이터에 역 FFT를 적용하여 시간 도메인 신호를 복원합니다.
     */
    public TimeDomainSignal computeInverseFFT(FrequencyDomainResult frequencyData) {
        int n = frequencyData.getFrequencies().length * 2; // Nyquist 정리에 따라 n/2 주파수 성분이 있음

        // 복소수 배열 구성
        Complex[] x = new Complex[n];
        for (int i = 0; i < frequencyData.getFrequencies().length; i++) {
            double magnitude = frequencyData.getMagnitudes()[i];
            double phase = frequencyData.getPhases()[i];
            x[i] = new Complex(
                    magnitude * Math.cos(phase),
                    magnitude * Math.sin(phase)
            );

            // 켤레 복소수 (Hermitian symmetry)
            if (i > 0 && i < n/2) {
                x[n-i] = new Complex(
                        magnitude * Math.cos(phase),
                        -magnitude * Math.sin(phase)
                );
            }
        }

        // IFFT 계산
        Complex[] result = ifft(x);

        // 실수 신호로 변환
        double[] realValues = new double[n];
        for (int i = 0; i < n; i++) {
            realValues[i] = result[i].getReal();
        }

        // 샘플링 레이트 복원 (원래 신호와 동일)
        double samplingRate = frequencyData.getFrequencies()[frequencyData.getFrequencies().length - 1] * 2;

        return new TimeDomainSignal(realValues, samplingRate);
    }

    /**
     * FFT 알고리즘의 각 단계를 시각화하기 위한 데이터를 생성합니다.
     */
    public List<FFTStep> getFFTSteps(TimeDomainSignal signal) {
        List<FFTStep> steps = new ArrayList<>();

        // 원래 신호를 첫 단계로 추가
        int n = findNextPowerOfTwo(signal.getRealValues().length);
        Complex[] x = new Complex[n];
        for (int i = 0; i < signal.getRealValues().length; i++) {
            x[i] = new Complex(signal.getRealValues()[i], 0);
        }
        for (int i = signal.getRealValues().length; i < n; i++) {
            x[i] = new Complex(0, 0);
        }

        steps.add(new FFTStep(0, x.clone(), "원본 신호 (필요시 제로 패딩됨)"));

        // FFT 각 레벨 시각화를 위한 내부 메서드 호출
        fftWithSteps(x, 1, steps);

        return steps;
    }

    /**
     * 주어진 숫자보다 크거나 같은 가장 작은 2의 거듭제곱을 찾습니다.
     */
    private int findNextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    /**
     * 분할 정복 FFT 알고리즘 구현
     */
    private Complex[] fft(Complex[] x) {
        int n = x.length;

        // 기저 사례: 길이가 1인 배열은 그대로 반환
        if (n == 1) return new Complex[] { x[0] };

        // 재귀적 분할: 짝수 인덱스와 홀수 인덱스 분리
        Complex[] even = new Complex[n/2];
        Complex[] odd = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
            odd[k] = x[2*k + 1];
        }

        // 재귀적으로 FFT 계산
        Complex[] evenResult = fft(even);
        Complex[] oddResult = fft(odd);

        // 결과 합치기
        Complex[] result = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            // 회전 인자 계산
            double theta = -2 * Math.PI * k / n;
            Complex wk = new Complex(Math.cos(theta), Math.sin(theta));

            // 결합
            Complex oddK = wk.multiply(oddResult[k]);
            result[k] = evenResult[k].add(oddK);
            result[k + n/2] = evenResult[k].subtract(oddK);
        }

        return result;
    }

    /**
     * 각 단계를 기록하는 FFT 알고리즘
     */
    private Complex[] fftWithSteps(Complex[] x, int level, List<FFTStep> steps) {
        int n = x.length;

        // 기저 사례: 길이가 1인 배열은 그대로 반환
        if (n == 1) return new Complex[] { x[0] };

        // 재귀적 분할: 짝수 인덱스와 홀수 인덱스 분리
        Complex[] even = new Complex[n/2];
        Complex[] odd = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
            odd[k] = x[2*k + 1];
        }

        // 분할 단계 기록
        steps.add(new FFTStep(level, even.clone(), "레벨 " + level + ": 짝수 인덱스 분할"));
        steps.add(new FFTStep(level, odd.clone(), "레벨 " + level + ": 홀수 인덱스 분할"));

        // 재귀적으로 FFT 계산
        Complex[] evenResult = fftWithSteps(even, level + 1, steps);
        Complex[] oddResult = fftWithSteps(odd, level + 1, steps);

        // 결과 합치기
        Complex[] result = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            // 회전 인자 계산
            double theta = -2 * Math.PI * k / n;
            Complex wk = new Complex(Math.cos(theta), Math.sin(theta));

            // 결합
            Complex oddK = wk.multiply(oddResult[k]);
            result[k] = evenResult[k].add(oddK);
            result[k + n/2] = evenResult[k].subtract(oddK);
        }

        // 결합 단계 기록
        steps.add(new FFTStep(level, result.clone(), "레벨 " + level + ": 결과 결합"));

        return result;
    }

    /**
     * 역 FFT 알고리즘 구현
     */
    private Complex[] ifft(Complex[] x) {
        int n = x.length;

        // 켤레 복소수 취하기
        Complex[] conjugated = new Complex[n];
        for (int i = 0; i < n; i++) {
            conjugated[i] = new Complex(x[i].getReal(), -x[i].getImag());
        }

        // FFT 계산
        Complex[] result = fft(conjugated);

        // 결과에 1/n을 곱하고 켤레 복소수 취하기
        for (int i = 0; i < n; i++) {
            result[i] = new Complex(result[i].getReal() / n, -result[i].getImag() / n);
        }

        return result;
    }
}