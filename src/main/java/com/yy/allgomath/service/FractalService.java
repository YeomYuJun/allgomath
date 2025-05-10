package com.yy.allgomath.service;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.datatype.FractalResult;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class FractalService {

    public FractalResult calculateMandelbrot(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations) {
        int[][] iterationCounts = new int[height][width];

        // 병렬 처리를 위한 스트림 활용
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                double real = xMin + (xMax - xMin) * x / width;
                double imag = yMin + (yMax - yMin) * y / height;

                Complex c = new Complex(real, imag);
                Complex z = new Complex(0, 0);

                int iteration = 0;
                while (iteration < maxIterations && z.magnitudeSquared() < 4) {
                    z = z.multiply(z).add(c);
                    iteration++;
                }

                iterationCounts[y][x] = iteration;
            }
        });

        return new FractalResult(width, height, iterationCounts);
    }

    // Julia 집합 계산 메소드도 유사하게 구현
    /**
     * 줄리아 집합을 계산합니다.
     *
     * @param xMin 실수부 최소값
     * @param xMax 실수부 최대값
     * @param yMin 허수부 최소값
     * @param yMax 허수부 최대값
     * @param cReal 고정 매개변수 c의 실수부
     * @param cImag 고정 매개변수 c의 허수부
     * @param width 이미지 너비
     * @param height 이미지 높이
     * @param maxIterations 최대 반복 횟수
     * @return 각 픽셀의 반복 횟수가 포함된 FractalResult 객체
     */
    public FractalResult calculateJulia(double xMin, double xMax, double yMin, double yMax,
                                        double cReal, double cImag, int width, int height,
                                        int maxIterations) {
        int[][] iterationCounts = new int[height][width];

        // 고정 매개변수 c
        Complex c = new Complex(cReal, cImag);

        // 병렬 처리를 위한 스트림 활용
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                // 복소평면 좌표 계산
                double real = xMin + (xMax - xMin) * x / width;
                double imag = yMin + (yMax - yMin) * y / height;

                // 이 좌표가 초기 z 값이 됨 (만델브로 집합과의 차이점)
                Complex z = new Complex(real, imag);

                int iteration = 0;
                // 발산 여부 테스트: |z|² > 4일 때 발산으로 간주
                while (iteration < maxIterations && z.magnitudeSquared() < 4) {
                    // z = z² + c 적용 (c는 고정 매개변수)
                    z = z.multiply(z).add(c);
                    iteration++;
                }

                iterationCounts[y][x] = iteration;
            }
        });

        return new FractalResult(width, height, iterationCounts);
    }
}