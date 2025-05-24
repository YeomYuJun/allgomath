package com.yy.allgomath.service;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.datatype.FractalResult;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.IntStream;

@Service
public class FractalService {

    /**
     * 만델브로 집합을 계산.
     * 
     * @param xMin 실수부 최소값
     * @param xMax 실수부 최대값
     * @param yMin 허수부 최소값
     * @param yMax 허수부 최대값
     * @param width 이미지 너비
     * @param height 이미지 높이
     * @param maxIterations 최대 반복 횟수
     * @param colorScheme 색상 스키마
     * @param smooth 부드러운 음영 적용 여부
     * @return 각 픽셀의 반복 횟수가 포함된 FractalResult 객체
     */
    public FractalResult calculateMandelbrot(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations,
                                             String colorScheme, boolean smooth) {
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

        return new FractalResult(width, height, iterationCounts, colorScheme, smooth);
    }

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
     * @param colorScheme 색상 스키마
     * @param smooth 부드러운 음영 적용 여부
     * @return 각 픽셀의 반복 횟수가 포함된 FractalResult 객체
     */
    public FractalResult calculateJulia(double xMin, double xMax, double yMin, double yMax,
                                        double cReal, double cImag, int width, int height,
                                        int maxIterations, String colorScheme, boolean smooth) {
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

        return new FractalResult(width, height, iterationCounts, colorScheme, smooth);
    }

    /**
     * 시에르핀스키 삼각형 계산 (Chaos Game 알고리즘 사용).
     *
     * @param xMin 실수부 최소값
     * @param xMax 실수부 최대값
     * @param yMin 허수부 최소값
     * @param yMax 허수부 최대값
     * @param width 이미지 너비
     * @param height 이미지 높이
     * @param maxIterations 점 생성 횟수
     * @param colorScheme 색상 스키마
     * @param smooth 부드러운 음영 적용 여부
     * @return 각 픽셀의 히트 카운트가 포함된 FractalResult 객체
     */
    public FractalResult calculateSierpinski(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations,
                                             String colorScheme, boolean smooth) {
        int[][] hitCounts = new int[height][width];

        // 시에르핀스키 삼각형의 3개 꼭짓점 정의 (정삼각형)
        double[] vertex1 = {0.0, 0.8};    // 상단
        double[] vertex2 = {-0.7, -0.4};  // 좌하단
        double[] vertex3 = {0.7, -0.4};   // 우하단

        // 시작점 (임의의 점)
        double currentX = 0.0;
        double currentY = 0.0;

        Random random = new Random();

        // Chaos Game 알고리즘 실행
        for (int i = 0; i < maxIterations; i++) {
            // 3개 꼭짓점 중 하나를 랜덤하게 선택
            int vertexIndex = random.nextInt(3);
            double[] selectedVertex;

            switch (vertexIndex) {
                case 0: selectedVertex = vertex1; break;
                case 1: selectedVertex = vertex2; break;
                default: selectedVertex = vertex3; break;
            }

            // 현재 점과 선택된 꼭짓점의 중점으로 이동
            currentX = (currentX + selectedVertex[0]) / 2.0;
            currentY = (currentY + selectedVertex[1]) / 2.0;

            // 처음 몇 번의 반복은 건너뛰기 (수렴을 위해)
            if (i < 100) continue;

            // 화면 좌표로 변환
            int pixelX = (int) ((currentX - xMin) / (xMax - xMin) * width);
            int pixelY = (int) ((currentY - yMin) / (yMax - yMin) * height);

            // 경계 검사
            if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
                hitCounts[pixelY][pixelX]++;
            }
        }

        return new FractalResult(width, height, hitCounts, colorScheme, smooth);
    }

    // 기존 메서드들에 대한 하위 호환성 유지
    public FractalResult calculateMandelbrot(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations) {
        return calculateMandelbrot(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }

    public FractalResult calculateJulia(double xMin, double xMax, double yMin, double yMax,
                                        double cReal, double cImag, int width, int height,
                                        int maxIterations) {
        return calculateJulia(xMin, xMax, yMin, yMax, cReal, cImag, width, height, maxIterations, "classic", true);
    }

    // 하위 호환성을 위한 오버로드 메서드
    public FractalResult calculateSierpinski(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations) {
        return calculateSierpinski(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }
}