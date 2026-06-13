package com.yy.allgomath.fractal;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.fractal.dto.TileData;
import com.yy.allgomath.fractal.dto.FractalParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TileCacheService {
    private static final int PRECISION = 10000; // 4 decimal places

    //DTO wrapper
    @Cacheable(value = "mandelbrot_tile",
            key = "#params.maxIterations + '_' + #params.smooth + '_' + " +
                    "T(Math).round(#tileXMin * " + PRECISION + ") + '_' + " +
                    "T(Math).round(#tileYMin * " + PRECISION + ") + '_' + " +
                    "T(Math).round(#tileXMax * " + PRECISION + ") + '_' + " +
                    "T(Math).round(#tileYMax * " + PRECISION + ")"
    )
    public TileData  calculateTile(FractalParameters params,
                                    double tileXMin, double tileYMin,
                                    double tileXMax, double tileYMax) {
        log.debug("calculateTile cache miss");
        log.debug("tile params: maxIter={}, smooth={}", params.getMaxIterations(), params.isSmooth());
        double[][] tileValues = new double[32][32];

        double pixelXStep = (tileXMax - tileXMin) / 32;
        double pixelYStep = (tileYMax - tileYMin) / 32;

        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                double real = tileXMin + x * pixelXStep;
                double imag = tileYMin + y * pixelYStep;

                Complex c = new Complex(real, imag);

                if (params.isSmooth()) {
                    tileValues[y][x] = calculateSmoothMandelbrot(c, params.getMaxIterations());
                } else {
                    tileValues[y][x] = calculateMandelbrot(c, params.getMaxIterations());
                }
            }
        }
        log.debug("calculateTile done");
        return new TileData(tileValues);
    }

    private double calculateMandelbrot(Complex c, int maxIterations) {
        Complex z = new Complex(0, 0);
        int iteration = 0;

        while (iteration < maxIterations && z.magnitudeSquared() < 4.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }

        return iteration == maxIterations ? -1 : iteration;
    }

    private double calculateSmoothMandelbrot(Complex c, int maxIterations) {
        Complex z = new Complex(0, 0);
        int iteration = 0;

        while (iteration < maxIterations && z.magnitudeSquared() < 256.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }

        if (iteration == maxIterations) {
            return -1;
        }

        double logZn = Math.log(z.magnitudeSquared()) / 2.0;
        double nu = Math.log(logZn / Math.log(2)) / Math.log(2);

        return iteration + 1 - nu;
    }
}