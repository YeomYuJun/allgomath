package com.yy.allgomath.service;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.datatype.FractalResult;
import com.yy.allgomath.datatype.Point2D;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 코흐 눈송이를 계산 (재귀적 선분 분할 방식).
     *
     * @param xMin 실수부 최소값
     * @param xMax 실수부 최대값
     * @param yMin 허수부 최소값
     * @param yMax 허수부 최대값
     * @param width 이미지 너비
     * @param height 이미지 높이
     * @param maxIterations 재귀 깊이 (권장: 1-7)
     * @param colorScheme 색상 스키마
     * @param smooth 부드러운 음영 적용 여부
     * @return 렌더링된 코흐 눈송이가 포함된 FractalResult 객체
     */
    public FractalResult calculateKoch(double xMin, double xMax, double yMin, double yMax,
                                       int width, int height, int maxIterations,
                                       String colorScheme, boolean smooth) {
        int[][] pixelData = new int[height][width];

        // 반복 횟수 제한 (성능 고려)
        int actualIterations = Math.min(maxIterations, 5); // 최대 5회로 더 제한

        // 초기 정삼각형의 꼭짓점 정의
        double size = 0.6; // 삼각형 크기
        double centerX = 0.0;
        double centerY = -0.1;

        // 정삼각형의 3개 꼭짓점 계산
        Point2D[] triangle = new Point2D[3];
        triangle[0] = new Point2D(centerX, centerY + size * 2/3);                    // 상단
        triangle[1] = new Point2D(centerX - size * Math.sqrt(3)/2, centerY - size/3); // 좌하단
        triangle[2] = new Point2D(centerX + size * Math.sqrt(3)/2, centerY - size/3); // 우하단

        // 각 변에 대해 코흐 곡선 생성 및 직접 렌더링
        for (int i = 0; i < 3; i++) {
            Point2D start = triangle[i];
            Point2D end = triangle[(i + 1) % 3];
            generateAndRenderKochCurve(start, end, actualIterations, pixelData, xMin, xMax, yMin, yMax, width, height);
        }


        return new FractalResult(width, height, pixelData, colorScheme, smooth);
    }

    /**
     * 코흐 곡선을 생성하고 직접 렌더링하는 최적화된 메서드
     */
    private void generateAndRenderKochCurve(Point2D start, Point2D end, int depth,
                                          int[][] pixelData, double xMin, double xMax,
                                          double yMin, double yMax, int width, int height) {
        if (depth == 0) {
            // 기본 케이스: 단순한 선분을 직접 렌더링
            renderLine(start, end, pixelData, xMin, xMax, yMin, yMax, width, height);
            return;
        }

        // 선분을 3등분
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();

        Point2D p1 = start;
        Point2D p2 = new Point2D(start.getX() + dx/3, start.getY() + dy/3);
        Point2D p4 = new Point2D(start.getX() + 2*dx/3, start.getY() + 2*dy/3);
        Point2D p5 = end;

        // 정삼각형의 꼭짓점 계산
        double mx = (p2.getX() + p4.getX()) / 2;
        double my = (p2.getY() + p4.getY()) / 2;
        double perpX = -(p4.getY() - p2.getY());
        double perpY = (p4.getX() - p2.getX());
        double triangleHeight = Math.sqrt(3) / 6 * Math.sqrt(dx*dx + dy*dy);
        double length = Math.sqrt(perpX*perpX + perpY*perpY);

        Point2D p3 = new Point2D(
                mx + perpX * triangleHeight / length,
                my + perpY * triangleHeight / length
        );

        // 재귀 호출
        generateAndRenderKochCurve(p1, p2, depth - 1, pixelData, xMin, xMax, yMin, yMax, width, height);
        generateAndRenderKochCurve(p2, p3, depth - 1, pixelData, xMin, xMax, yMin, yMax, width, height);
        generateAndRenderKochCurve(p3, p4, depth - 1, pixelData, xMin, xMax, yMin, yMax, width, height);
        generateAndRenderKochCurve(p4, p5, depth - 1, pixelData, xMin, xMax, yMin, yMax, width, height);
    }

    /**
     * 두 점 사이의 선분을 직접 렌더링
     */
    private void renderLine(Point2D start, Point2D end, int[][] pixelData,
                          double xMin, double xMax, double yMin, double yMax,
                          int width, int height) {
        // 월드 좌표를 픽셀 좌표로 변환
        int x1 = (int) ((start.getX() - xMin) / (xMax - xMin) * width);
        int y1 = (int) ((start.getY() - yMin) / (yMax - yMin) * height);
        int x2 = (int) ((end.getX() - xMin) / (xMax - xMin) * width);
        int y2 = (int) ((end.getY() - yMin) / (yMax - yMin) * height);

        // Bresenham 라인 알고리즘으로 선분 그리기
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
                pixelData[y1][x1] = 255;
            }

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    // 하위 호환성을 위한 오버로드 메서드
    public FractalResult calculateKoch(double xMin, double xMax, double yMin, double yMax,
                                       int width, int height, int maxIterations) {
        return calculateKoch(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }

    /**
     * 반슬리 고사리를 계산 (IFS 알고리즘 사용).
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
    public FractalResult calculateBarnsley(double xMin, double xMax, double yMin, double yMax,
                                          int width, int height, int maxIterations,
                                          String colorScheme, boolean smooth) {
        int[][] hitCounts = new int[height][width];

        // 시작점
        double x = 0.0;
        double y = 0.0;

        Random random = new Random();

        // IFS 변환 행렬과 확률
        double[][] transforms = {
            {0.0, 0.0, 0.0, 0.16, 0.0, 0.0, 0.01},    // 줄기
            {0.85, 0.04, -0.04, 0.85, 0.0, 1.6, 0.85}, // 작은 잎
            {0.2, -0.26, 0.23, 0.22, 0.0, 1.6, 0.07},  // 왼쪽 잎
            {-0.15, 0.28, 0.26, 0.24, 0.0, 0.44, 0.07}  // 오른쪽 잎
        };

        // 처음 몇 번의 반복은 건너뛰기 (수렴을 위해)
        for (int i = 0; i < 100; i++) {
            double r = random.nextDouble();
            double sum = 0.0;
            int transformIndex = 0;

            // 확률에 따른 변환 선택
            for (int j = 0; j < transforms.length; j++) {
                sum += transforms[j][6];
                if (r < sum) {
                    transformIndex = j;
                    break;
                }
            }

            // 선택된 변환 적용
            double newX = transforms[transformIndex][0] * x + transforms[transformIndex][1] * y + transforms[transformIndex][4];
            double newY = transforms[transformIndex][2] * x + transforms[transformIndex][3] * y + transforms[transformIndex][5];
            x = newX;
            y = newY;
        }

        // 메인 반복
        for (int i = 0; i < maxIterations; i++) {
            double r = random.nextDouble();
            double sum = 0.0;
            int transformIndex = 0;

            // 확률에 따른 변환 선택
            for (int j = 0; j < transforms.length; j++) {
                sum += transforms[j][6];
                if (r < sum) {
                    transformIndex = j;
                    break;
                }
            }

            // 선택된 변환 적용
            double newX = transforms[transformIndex][0] * x + transforms[transformIndex][1] * y + transforms[transformIndex][4];
            double newY = transforms[transformIndex][2] * x + transforms[transformIndex][3] * y + transforms[transformIndex][5];
            x = newX;
            y = newY;

            // 화면 좌표로 변환
            int pixelX = (int) ((x - xMin) / (xMax - xMin) * width);
            int pixelY = (int) ((y - yMin) / (yMax - yMin) * height);

            // 경계 검사
            if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
                hitCounts[pixelY][pixelX]++;
            }
        }

        return new FractalResult(width, height, hitCounts, colorScheme, smooth);
    }

    // 하위 호환성을 위한 오버로드 메서드
    public FractalResult calculateBarnsley(double xMin, double xMax, double yMin, double yMax,
                                          int width, int height, int maxIterations) {
        return calculateBarnsley(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }
}