package com.yy.allgomath.service;

import com.yy.allgomath.cache.service.FFTCacheService;
import com.yy.allgomath.datatype.*;
import com.yy.allgomath.fft.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FFTService {

    @Autowired
    private FFTCacheService fftCacheService;
    
    
    /**
     * 캐시 조회 후 계산    
     * @param signal 입력 시간 도메인 신호 (TimeDomainSignal 객체)
     * @return 주파수 도메인 결과 (FrequencyDomainResult 객체)
     */
    public FrequencyDomainResult computeFFTWithCache(TimeDomainSignal signal) {
        String cacheKey = makeFFTCacheKey(signal);

        // 1. 캐시 조회
        FrequencyDomainResult cached = (FrequencyDomainResult) fftCacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 2. 캐시에 없으면 계산
        FrequencyDomainResult result = computeFFT(signal);

        // 3. 캐시에 저장
        fftCacheService.set(cacheKey, result);

        return result;
    }

    

    /**
     * 시간 도메인 신호에 FFT를 적용하여 주파수 도메인 결과를 계산.
     *
     * @param signal 입력 시간 도메인 신호 (TimeDomainSignal 객체)
     * @return 주파수 도메인 결과 (FrequencyDomainResult 객체)
     *         - frequencies: 주파수 성분 배열 (Hz)
     *         - magnitudes: 각 주파수 성분의 진폭 배열
     *         - phases: 각 주파수 성분의 위상 배열 (라디안)
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
     * 주파수 도메인 데이터에 역 FFT를 적용하여 시간 도메인 신호를 복원.
     *
     * @param frequencyData 입력 주파수 도메인 데이터 (FrequencyDomainResult 객체)
     * @return 복원된 시간 도메인 신호 (TimeDomainSignal 객체)
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
     *
     * @param signal 입력 시간 도메인 신호 (TimeDomainSignal 객체)
     * @return 각 단계의 데이터를 포함한 리스트 (FFTStep 객체)
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
     *
     * @param n 입력 숫자
     * @return 주어진 숫자보다 크거나 같은 가장 작은 2의 거듭제곱
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
     *
     * @param x 입력 복소수 배열
     * @return 주파수 도메인 결과 (Complex 배열)
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
     *
     * @param x 입력 복소수 배열
     * @param level 현재 레벨
     * @param steps 각 단계를 기록하는 리스트
     * @return 주파수 도메인 결과 (Complex 배열)
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
     *
     * @param x 입력 복소수 배열
     * @return 주파수 도메인 결과 (Complex 배열)
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

    /**
     * 신호 감기 시각화 데이터 생성
     */
    public WindingVisualizationResult computeWindingVisualization(WindingRequest request) {
        List<Double> signal = request.getSignal();
        double windingFreq = request.getWindingFrequency();
        double samplingRate = request.getSamplingRate();
        double duration = request.getDuration();

        List<WindingVisualizationResult.WindingPoint> windingPath = new ArrayList<>();
        List<Double> timePoints = new ArrayList<>();

        // 시간 도메인 신호를 복소평면에 감기
        for (int i = 0; i < signal.size(); i++) {
            double t = i / samplingRate;
            double amplitude = signal.get(i);

            // 오일러 공식: e^(-2πift) = cos(-2πft) + i*sin(-2πft)
            double theta = -2 * Math.PI * windingFreq * t;
            double real = amplitude * Math.cos(theta);
            double imaginary = amplitude * Math.sin(theta);

            windingPath.add(new WindingVisualizationResult.WindingPoint(
                    real, imaginary, t, amplitude));
            timePoints.add(t);
        }

        // 질량 중심 계산 (복소수 적분/평균)
        WindingVisualizationResult.CenterOfMass centerOfMass = calculateCenterOfMass(windingPath);

        return new WindingVisualizationResult(
                windingPath, centerOfMass, windingFreq, timePoints, signal);
    }

    /**
     * 질량 중심 계산
     */
    private WindingVisualizationResult.CenterOfMass calculateCenterOfMass(
            List<WindingVisualizationResult.WindingPoint> windingPath) {

        double realSum = 0.0;
        double imaginarySum = 0.0;

        for (WindingVisualizationResult.WindingPoint point : windingPath) {
            realSum += point.getReal();
            imaginarySum += point.getImaginary();
        }

        double realAvg = realSum / windingPath.size();
        double imaginaryAvg = imaginarySum / windingPath.size();

        return new WindingVisualizationResult.CenterOfMass(realAvg, imaginaryAvg);
    }

    /**
     * 주파수 스윕을 통한 완전한 FFT 시각화 데이터 생성
     */
    public FFTSweepResult computeFrequencySweep(TimeDomainSignal signal,
                                                double minFreq, double maxFreq, int steps) {
        List<WindingVisualizationResult> sweepResults = new ArrayList<>();
        List<Double> frequencies = new ArrayList<>();
        List<Double> magnitudes = new ArrayList<>();

        double freqStep = (maxFreq - minFreq) / steps;

        for (int i = 0; i <= steps; i++) {
            double freq = minFreq + i * freqStep;

            WindingRequest request = new WindingRequest(
                    Arrays.stream(signal.getRealValues()).boxed().collect(Collectors.toList()),
                    signal.getSamplingRate(),
                    freq,
                    signal.getRealValues().length / signal.getSamplingRate()
            );

            WindingVisualizationResult result = computeWindingVisualization(request);
            sweepResults.add(result);
            frequencies.add(freq);
            magnitudes.add(result.getCenterOfMass().getMagnitude());
        }

        return new FFTSweepResult(sweepResults, frequencies, magnitudes);
    }

    /**
     * 다중 주파수 신호 생성 (테스트용)
     */
    public TimeDomainSignal generateMultiFrequencySignal(
            List<Double> frequencies, List<Double> amplitudes,
            double samplingRate, double duration) {

        int numSamples = (int)(samplingRate * duration);
        double[] signal = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double t = i / samplingRate;
            double value = 0.0;

            for (int j = 0; j < frequencies.size(); j++) {
                double freq = frequencies.get(j);
                double amp = j < amplitudes.size() ? amplitudes.get(j) : 1.0;
                value += amp * Math.sin(2 * Math.PI * freq * t);
            }

            signal[i] = value;
        }

        return new TimeDomainSignal(signal, samplingRate);
    }

    /**
     * 주파수 스윕 결과 데이터 구조
     */
    public static class FFTSweepResult {
        private List<WindingVisualizationResult> sweepResults;
        private List<Double> frequencies;
        private List<Double> magnitudes;

        public FFTSweepResult(List<WindingVisualizationResult> sweepResults,
                              List<Double> frequencies, List<Double> magnitudes) {
            this.sweepResults = sweepResults;
            this.frequencies = frequencies;
            this.magnitudes = magnitudes;
        }

        // Getters and Setters
        public List<WindingVisualizationResult> getSweepResults() { return sweepResults; }
        public List<Double> getFrequencies() { return frequencies; }
        public List<Double> getMagnitudes() { return magnitudes; }
    }
    // FFTService.java에 추가할 메서드들

    /**
     * 주파수 피크 검출
     */
    public List<FrequencyPeak> detectFrequencyPeaks(FrequencyDomainResult fftResult, double threshold) {
        List<FrequencyPeak> peaks = new ArrayList<>();

        double[] frequencies = fftResult.getFrequencies();
        double[] magnitudes = fftResult.getMagnitudes();
        double[] phases = fftResult.getPhases();

        // 최대 크기 기준으로 임계값 계산
        double maxMagnitude = 0;
        for (double magnitude : magnitudes) {
            maxMagnitude = Math.max(maxMagnitude, magnitude);
        }
        double actualThreshold = maxMagnitude * threshold;

        // 피크 검출 (지역 최대값 찾기)
        for (int i = 1; i < magnitudes.length - 1; i++) {
            if (magnitudes[i] > magnitudes[i-1] &&
                    magnitudes[i] > magnitudes[i+1] &&
                    magnitudes[i] > actualThreshold) {

                // 날카로움 계산 (피크의 폭 역수)
                double sharpness = magnitudes[i] / (magnitudes[i-1] + magnitudes[i+1]);

                peaks.add(new FrequencyPeak(
                        frequencies[i],
                        magnitudes[i],
                        phases[i],
                        sharpness
                ));
            }
        }

        // 크기 순으로 정렬
        peaks.sort((a, b) -> Double.compare(b.getMagnitude(), a.getMagnitude()));

        return peaks;
    }

    /**
     * 신호 특성 분석
     */
    public SignalCharacteristics analyzeSignalCharacteristics(TimeDomainSignal signal) {
        double[] values = signal.getRealValues();

        // 총 에너지 계산
        double totalEnergy = 0;
        for (double value : values) {
            totalEnergy += value * value;
        }

        // 평균 진폭 계산
        double sum = 0;
        for (double value : values) {
            sum += Math.abs(value);
        }
        double averageAmplitude = sum / values.length;

        // 최대 진폭 계산
        double peakAmplitude = 0;
        for (double value : values) {
            peakAmplitude = Math.max(peakAmplitude, Math.abs(value));
        }

        // 신호 대 잡음비 추정 (간단한 방법)
        double signalPower = totalEnergy / values.length;
        double noisePower = estimateNoisePower(values);
        double snr = noisePower > 0 ? 10 * Math.log10(signalPower / noisePower) : Double.POSITIVE_INFINITY;

        // 주요 주파수 성분 찾기
        FrequencyDomainResult fftResult = computeFFT(signal);
        List<FrequencyPeak> peaks = detectFrequencyPeaks(fftResult, 0.1);
        List<Double> dominantFrequencies = peaks.stream()
                .limit(5)
                .map(FrequencyPeak::getFrequency)
                .collect(Collectors.toList());

        return new SignalCharacteristics(
                totalEnergy, averageAmplitude, peakAmplitude, snr, dominantFrequencies
        );
    }

    /**
     * 잡음 전력 추정 (고주파 성분 기반)
     */
    private double estimateNoisePower(double[] signal) {
        // 간단한 방법: 고주파 대역의 평균 전력을 잡음으로 간주
        FrequencyDomainResult fft = computeFFT(new TimeDomainSignal(signal, 1.0));
        double[] magnitudes = fft.getMagnitudes();

        // 상위 25% 주파수 대역을 잡음으로 간주
        int startIndex = magnitudes.length * 3 / 4;
        double noisePower = 0;
        int count = 0;

        for (int i = startIndex; i < magnitudes.length; i++) {
            noisePower += magnitudes[i] * magnitudes[i];
            count++;
        }

        return count > 0 ? noisePower / count : 0;
    }

    /**
     * 교육용 데모 생성
     */
    public EducationalDemoResult generateEducationalDemo(
            String demoType, double samplingRate, double duration) {

        List<String> explanations = new ArrayList<>();
        TimeDomainSignal originalSignal;
        List<TimeDomainSignal> componentSignals = new ArrayList<>();

        switch (demoType.toLowerCase()) {
            case "basic":
                // 기본: 단일 사인파
                originalSignal = generateMultiFrequencySignal(
                        Arrays.asList(5.0), Arrays.asList(1.0), samplingRate, duration);
                componentSignals.add(originalSignal);
                explanations.add("단순한 5Hz 사인파입니다.");
                explanations.add("FFT 결과에서 5Hz에 스파이크가 나타납니다.");
                break;

            case "dual":
                // 이중 주파수
                originalSignal = generateMultiFrequencySignal(
                        Arrays.asList(3.0, 7.0), Arrays.asList(1.0, 0.7), samplingRate, duration);
                componentSignals.add(generateMultiFrequencySignal(
                        Arrays.asList(3.0), Arrays.asList(1.0), samplingRate, duration));
                componentSignals.add(generateMultiFrequencySignal(
                        Arrays.asList(7.0), Arrays.asList(0.7), samplingRate, duration));
                explanations.add("3Hz와 7Hz 사인파의 합성입니다.");
                explanations.add("각 주파수에서 별도의 스파이크가 나타납니다.");
                break;

            case "complex":
                // 복잡한 신호
                originalSignal = generateMultiFrequencySignal(
                        Arrays.asList(2.0, 5.0, 8.0, 12.0),
                        Arrays.asList(1.0, 0.8, 0.5, 0.3),
                        samplingRate, duration);
                for (int i = 0; i < 4; i++) {
                    componentSignals.add(generateMultiFrequencySignal(
                            Arrays.asList(Arrays.asList(2.0, 5.0, 8.0, 12.0).get(i)),
                            Arrays.asList(Arrays.asList(1.0, 0.8, 0.5, 0.3).get(i)),
                            samplingRate, duration));
                }
                explanations.add("4개 주파수 성분의 복잡한 합성입니다.");
                explanations.add("각 성분이 FFT에서 개별적으로 식별됩니다.");
                break;

            default:
                originalSignal = generateMultiFrequencySignal(
                        Arrays.asList(5.0), Arrays.asList(1.0), samplingRate, duration);
                componentSignals.add(originalSignal);
                explanations.add("기본 데모입니다.");
        }

        // FFT 계산
        FrequencyDomainResult fftResult = computeFFT(originalSignal);

        return new EducationalDemoResult(
                demoType,
                "교육용 " + demoType + " 데모",
                originalSignal,
                componentSignals,
                fftResult,
                explanations
        );
    }

    /**
     * 신호 합성 시뮬레이션
     */
    public SignalCompositionResult simulateSignalComposition(
            SignalCompositionRequest request) {

        // 개별 성분 신호들 생성
        List<TimeDomainSignal> individualComponents = new ArrayList<>();
        List<FrequencyDomainResult> componentFFTs = new ArrayList<>();

        for (var component : request.getComponents()) {
            TimeDomainSignal componentSignal = generateMultiFrequencySignal(
                    Arrays.asList(component.getFrequency()),
                    Arrays.asList(component.getAmplitude()),
                    request.getSamplingRate(),
                    request.getDuration()
            );

            individualComponents.add(componentSignal);
            componentFFTs.add(computeFFT(componentSignal));
        }

        // 합성 신호 생성
        List<Double> frequencies = request.getComponents().stream()
                .map(comp -> comp.getFrequency())
                .collect(Collectors.toList());
        List<Double> amplitudes = request.getComponents().stream()
                .map(comp -> comp.getAmplitude())
                .collect(Collectors.toList());

        TimeDomainSignal composedSignal = generateMultiFrequencySignal(
                frequencies, amplitudes, request.getSamplingRate(), request.getDuration());

        // 노이즈 추가 (선택적)
        if (request.isIncludeNoise()) {
            composedSignal = addNoise(composedSignal, request.getNoiseLevel());
        }

        // 합성 신호의 FFT
        FrequencyDomainResult composedFFT = computeFFT(composedSignal);

        // 분석 결과
        Map<String, Object> analysisResults = new HashMap<>();
        analysisResults.put("componentCount", request.getComponents().size());
        analysisResults.put("totalEnergy", calculateTotalEnergy(composedSignal));
        analysisResults.put("peakFrequencies", frequencies);
        analysisResults.put("noiseAdded", request.isIncludeNoise());

        return new SignalCompositionResult(
                composedSignal,
                individualComponents,
                composedFFT,
                componentFFTs,
                analysisResults
        );
    }

    /**
     * 신호에 노이즈 추가
     */
    private TimeDomainSignal addNoise(TimeDomainSignal signal, double noiseLevel) {
        double[] values = signal.getRealValues();
        double[] noisyValues = new double[values.length];
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < values.length; i++) {
            double noise = random.nextGaussian() * noiseLevel;
            noisyValues[i] = values[i] + noise;
        }

        return new TimeDomainSignal(noisyValues, signal.getSamplingRate());
    }

    /**
     * 신호의 총 에너지 계산
     */
    private double calculateTotalEnergy(TimeDomainSignal signal) {
        double totalEnergy = 0;
        for (double value : signal.getRealValues()) {
            totalEnergy += value * value;
        }
        return totalEnergy;
    }

    /*
     * 캐시 키 생성
     */
    private String makeFFTCacheKey(TimeDomainSignal signal) {
        // 신호+샘플링레이트 직렬화 후 SHA-256 해시
        StringBuilder sb = new StringBuilder();
        for (double v : signal.getRealValues()) {
            sb.append(v).append(",");
        }
        sb.append("|").append(signal.getSamplingRate());
        // 필요시 duration 등 추가
        return "fft:" + sha256(sb.toString());
    }

    /*
     * SHA-256 해시 생성
     */
    private String sha256(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}