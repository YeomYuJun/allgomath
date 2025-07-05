package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.fractal.FractalParameters;
import com.yy.allgomath.service.TileCacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 만델브로 집합 계산기
 */
@Component
public class MandelbrotCalculator implements FractalCalculator {
    //기존 만델브로 캐싱 사이즈가 너무 크기 떄문에 조금 더 상세한 캐싱 유도해야함.
    private static final int TILE_SIZE = 32; // 32x32 타일
    private final TileCacheService tileCacheService;

    public MandelbrotCalculator(TileCacheService tileCacheService) {
        this.tileCacheService = tileCacheService;
    }


    //depreacted..될 예정 캐싱 전략 추가해야함.
    @Override
    public double[][] calculate(FractalParameters params) {
        validateParameters(params);

        double[][] values = new double[params.getHeight()][params.getWidth()];

        // 병렬 처리로 성능 최적화
        IntStream.range(0, params.getHeight()).parallel().forEach(y -> {
            for (int x = 0; x < params.getWidth(); x++) {
                double real = params.getXMin() + (params.getXMax() - params.getXMin()) * x / params.getWidth();
                double imag = params.getYMin() + (params.getYMax() - params.getYMin()) * y / params.getHeight();

                Complex c = new Complex(real, imag);

                if (params.isSmooth()) {
                    values[y][x] = calculateSmoothMandelbrot(c, params.getMaxIterations());
                } else {
                    values[y][x] = calculateMandelbrot(c, params.getMaxIterations());
                }
            }
        });

        return values;
    }

    //    deprecated 1,2
    //    version1
    //    @Cacheable(value = "mandelbrot",
    //            key = "#params.width + '_' + #params.maxIterations + '_' + #params.smooth")
    //    version2
    //    @Cacheable(value = "mandelbrot", key = "#params.width + '_' + #params.maxIterations")
    @Override
    public double[][] calculateWithCaching(FractalParameters params) {
        System.out.println("🔍 calculateWithCaching 시작 - 해상도: " + params.getWidth() + "x" + params.getHeight());

        try {
            validateParameters(params);

            double[][] values = new double[params.getHeight()][params.getWidth()];
            System.out.println("✅ 빈 배열 생성 완료");

            int tilesX = (params.getWidth() + TILE_SIZE - 1) / TILE_SIZE;
            int tilesY = (params.getHeight() + TILE_SIZE - 1) / TILE_SIZE;
            System.out.println("📐 타일 계산: " + tilesX + "x" + tilesY + " = " + (tilesX * tilesY) + "개");

            List<TileResult> tileResults = IntStream.range(0, tilesX * tilesY)
                    .parallel()
                    .mapToObj(tileIndex -> {
                        int tileX = tileIndex % tilesX;
                        int tileY = tileIndex / tilesX;

                        try {
                            double tileXMin = calculateTileXMin(params, tileX);
                            double tileYMin = calculateTileYMin(params, tileY);
                            double tileXMax = calculateTileXMax(params, tileX);
                            double tileYMax = calculateTileYMax(params, tileY);
                            double[][] tileValues = tileCacheService.calculateTile(
                                    params, tileXMin, tileYMin, tileXMax, tileYMax);
                            if (tileValues == null) {
                                System.err.println("❌ 타일 계산 결과가 null: " + tileX + ", " + tileY);
                                return null;
                            }
                            return new TileResult(tileX, tileY, tileValues);
                        } catch (Exception e) {
                            System.err.println("❌ 타일 계산 오류 (" + tileX + ", " + tileY + "): " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(result -> result != null) // null 결과 제거
                    .toList();

            System.out.println("✅ 타일 계산 완료: " + tileResults.size() + "/" + (tilesX * tilesY));

            // 타일 복사
            tileResults.forEach(result -> {
                try {
                    copyTileToArray(values, result.values, result.tileX, result.tileY, params);
                } catch (Exception e) {
                    System.err.println("❌ 타일 복사 오류: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            System.out.println("✅ calculateWithCaching 완료");
            return values;

        } catch (Exception e) {
            System.err.println("🚨 calculateWithCaching 전체 오류: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    // 타일 결과를 담는 내부 클래스
    private static class TileResult {
        final int tileX;
        final int tileY;
        final double[][] values;

        TileResult(int tileX, int tileY, double[][] values) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.values = values;
        }
    }

    @Cacheable(value = "mandelbrot_tile",
            key = "#params.maxIterations + '_' + #params.smooth + '_' + " +
                    "T(Math).round(calculateTileXMin(#params, #tileX) * 10000) + '_' + " +
                    "T(Math).round(calculateTileYMin(#params, #tileY) * 10000)")
    public double[][] calculateTileCached(FractalParameters params, int tileX, int tileY) {
        // 32x32 타일 계산
        double[][] tileValues = new double[TILE_SIZE][TILE_SIZE];

        double tileXMin = calculateTileXMin(params, tileX);
        double tileYMin = calculateTileYMin(params, tileY);
        double tileXStep = (params.getXMax() - params.getXMin()) / params.getWidth() * TILE_SIZE;
        double tileYStep = (params.getYMax() - params.getYMin()) / params.getHeight() * TILE_SIZE;

        for (int y = 0; y < TILE_SIZE; y++) {
            for (int x = 0; x < TILE_SIZE; x++) {
                double real = tileXMin + x * tileXStep / TILE_SIZE;
                double imag = tileYMin + y * tileYStep / TILE_SIZE;

                Complex c = new Complex(real, imag);

                if (params.isSmooth()) {
                    tileValues[y][x] = calculateSmoothMandelbrot(c, params.getMaxIterations());
                } else {
                    tileValues[y][x] = calculateMandelbrot(c, params.getMaxIterations());
                }
            }
        }

        return tileValues;
    }

    /**
     * 타일의 X 좌표 최소값 계산
     */
    public double calculateTileXMin(FractalParameters params, int tileX) {
        double totalWidth = params.getXMax() - params.getXMin();
        double tileWidth = totalWidth / Math.ceil((double)params.getWidth() / TILE_SIZE);
        return params.getXMin() + tileX * tileWidth;
    }

    /**
     * 타일의 X 좌표 최대값 계산
     */
    public double calculateTileXMax(FractalParameters params, int tileX) {
        double totalWidth = params.getXMax() - params.getXMin();
        double tileWidth = totalWidth / Math.ceil((double)params.getWidth() / TILE_SIZE);
        return params.getXMin() + (tileX + 1) * tileWidth;
    }

    /**
     * 타일의 Y 좌표 최소값 계산
     */
    public double calculateTileYMin(FractalParameters params, int tileY) {
        double totalHeight = params.getYMax() - params.getYMin();
        double tileHeight = totalHeight / Math.ceil((double)params.getHeight() / TILE_SIZE);
        return params.getYMin() + tileY * tileHeight;
    }

    /**
     * 타일의 Y 좌표 최대값 계산
     */
    public double calculateTileYMax(FractalParameters params, int tileY) {
        double totalHeight = params.getYMax() - params.getYMin();
        double tileHeight = totalHeight / Math.ceil((double)params.getHeight() / TILE_SIZE);
        return params.getYMin() + (tileY + 1) * tileHeight;
    }

    /**
     * 타일 결과를 전체 배열에 복사
     */
    private void copyTileToArray(double[][] targetArray, double[][] tileValues,
                                 int tileX, int tileY, FractalParameters params) {

        int startX = tileX * TILE_SIZE;
        int startY = tileY * TILE_SIZE;

        int endX = Math.min(startX + TILE_SIZE, params.getWidth());
        int endY = Math.min(startY + TILE_SIZE, params.getHeight());

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int tileLocalX = x - startX;
                int tileLocalY = y - startY;

                // 배열 경계 체크
                if (tileLocalX < TILE_SIZE && tileLocalY < TILE_SIZE) {
                    targetArray[y][x] = tileValues[tileLocalY][tileLocalX];
                }
            }
        }
    }







    
    @Override
    public String getSupportedType() {
        return "mandelbrot";
    }
    
    @Override
    public String getDescription() {
        return "만델브로 집합 - 복소수 c에 대해 z(n+1) = z(n)² + c 수열의 발산 여부를 계산";
    }
    
    /**
     * 일반적인 만델브로 계산 (정수 반복 횟수)
     */
    private double calculateMandelbrot(Complex c, int maxIterations) {
        Complex z = new Complex(0, 0);
        int iteration = 0;
        
        while (iteration < maxIterations && z.magnitudeSquared() < 4.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }
        
        return iteration == maxIterations ? -1 : iteration;
    }
    
    /**
     * 부드러운 만델브로 계산 (연속적인 값)
     */
    private double calculateSmoothMandelbrot(Complex c, int maxIterations) {
        Complex z = new Complex(0, 0);
        int iteration = 0;
        
        while (iteration < maxIterations && z.magnitudeSquared() < 256.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }
        
        if (iteration == maxIterations) {
            return -1; // 수렴하는 점
        }
        
        // 부드러운 반복 횟수 계산
        double logZn = Math.log(z.magnitudeSquared()) / 2.0;
        double nu = Math.log(logZn / Math.log(2)) / Math.log(2);
        
        return iteration + 1 - nu;
    }
    
    /**
     * 카디오이드와 구근 최적화 (성능 향상)
     * 만델브로 집합의 주요 구성 요소에 속하는 점들을 빠르게 식별
     */
    private boolean isInMainCardioidOrBulb(Complex c) {
        double x = c.getReal();
        double y = c.getImag();
        
        // 메인 카디오이드 체크
        double q = Math.pow(x - 0.25, 2) + y * y;
        if (q * (q + (x - 0.25)) < 0.25 * y * y) {
            return true;
        }
        
        // 좌측 구근 체크
        if (Math.pow(x + 1, 2) + y * y < 0.0625) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 최적화된 만델브로 계산 (선택적으로 사용 가능)
     */
    private double calculateOptimizedMandelbrot(Complex c, int maxIterations) {
        // 빠른 배제를 위한 사전 체크
        if (isInMainCardioidOrBulb(c)) {
            return -1; // 확실히 집합 내부
        }
        
        return calculateSmoothMandelbrot(c, maxIterations);
    }
}