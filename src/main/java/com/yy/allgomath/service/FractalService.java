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

        // 초기 정삼각형의 꼭짓점 정의
        double size = 0.6; // 삼각형 크기
        double centerX = 0.0;
        double centerY = -0.1;

        // 정삼각형의 3개 꼭짓점 계산
        Point2D[] triangle = new Point2D[3];
        triangle[0] = new Point2D(centerX, centerY + size * 2/3);                    // 상단
        triangle[1] = new Point2D(centerX - size * Math.sqrt(3)/2, centerY - size/3); // 좌하단
        triangle[2] = new Point2D(centerX + size * Math.sqrt(3)/2, centerY - size/3); // 우하단

        // 각 변에 대해 코흐 곡선 생성
        List<Point2D> allPoint2Ds = new ArrayList<>();

        // 삼각형의 3개 변을 각각 코흐 곡선으로 변환
        for (int i = 0; i < 3; i++) {
            Point2D start = triangle[i];
            Point2D end = triangle[(i + 1) % 3];
            List<Point2D> kochCurve = generateKochCurve(start, end, maxIterations);
            allPoint2Ds.addAll(kochCurve);
        }

        // 점들을 픽셀 데이터로 렌더링
        renderPoint2DsToPixels(allPoint2Ds, pixelData, xMin, xMax, yMin, yMax, width, height);

        return new FractalResult(width, height, pixelData, colorScheme, smooth);
    }

    /**
     * 두 점 사이에 코흐 곡선을 재귀적으로 생성
     */
    private List<Point2D> generateKochCurve(Point2D start, Point2D end, int depth) {
        List<Point2D> Point2Ds = new ArrayList<>();

        if (depth == 0) {
            // 기본 케이스: 단순한 선분
            Point2Ds.add(start);
            Point2Ds.add(end);
        } else {
            // 선분을 3등분
            double dx = end.getX() - start.getX();
            double dy = end.getY() - start.getY();

            Point2D p1 = start;
            Point2D p2 = new Point2D(start.getX() + dx/3, start.getY() + dy/3);
            Point2D p4 = new Point2D(start.getX() + 2*dx/3, start.getY() + 2*dy/3);
            Point2D p5 = end;

            // 정삼각형의 꼭짓점 계산 (p2와 p4를 밑변으로 하는)
            double mx = (p2.getX() + p4.getX()) / 2; // 중점
            double my = (p2.getY() + p4.getY()) / 2;

            // 수직 벡터 (90도 회전)
            double perpX = -(p4.getY() - p2.getY());
            double perpY = (p4.getX() - p2.getX());

            // 정삼각형 높이
            double height = Math.sqrt(3) / 6 * Math.sqrt(dx*dx + dy*dy);
            double length = Math.sqrt(perpX*perpX + perpY*perpY);

            Point2D p3 = new Point2D(
                    mx + perpX * height / length,
                    my + perpY * height / length
            );

            // 4개 선분에 대해 재귀 호출
            Point2Ds.addAll(generateKochCurve(p1, p2, depth - 1));
            Point2Ds.addAll(generateKochCurve(p2, p3, depth - 1));
            Point2Ds.addAll(generateKochCurve(p3, p4, depth - 1));
            Point2Ds.addAll(generateKochCurve(p4, p5, depth - 1));
        }

        return Point2Ds;
    }

    /**
     * 점들을 픽셀 배열에 렌더링 (Bresenham 라인 알고리즘 사용).
     */
    private void renderPoint2DsToPixels(List<Point2D> Point2Ds, int[][] pixelData,
                                        double xMin, double xMax, double yMin, double yMax,
                                        int width, int height) {
        for (int i = 0; i < Point2Ds.size() - 1; i++) {
            Point2D p1 = Point2Ds.get(i);
            Point2D p2 = Point2Ds.get(i + 1);

            // 월드 좌표를 픽셀 좌표로 변환
            int x1 = (int) ((p1.getX() - xMin) / (xMax - xMin) * width);
            int y1 = (int) ((p1.getX() - yMin) / (yMax - yMin) * height);
            int x2 = (int) ((p2.getX() - xMin) / (xMax - xMin) * width);
            int y2 = (int) ((p2.getX() - yMin) / (yMax - yMin) * height);

            // Bresenham 라인 그리기
            drawLine(pixelData, x1, y1, x2, y2, width, height, 255); // 최대 강도로 설정
        }
    }

    /**
     * Bresenham 라인 알고리즘으로 선분을 그림
     */
    private void drawLine(int[][] pixels, int x0, int y0, int x1, int y1,
                          int width, int height, int intensity) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // 경계 검사 후 픽셀 설정
            if (x0 >= 0 && x0 < width && y0 >= 0 && y0 < height) {
                pixels[y0][x0] = intensity;
            }

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    // 하위 호환성을 위한 오버로드 메서드
    public FractalResult calculateKoch(double xMin, double xMax, double yMin, double yMax,
                                       int width, int height, int maxIterations) {
        return calculateKoch(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }
}