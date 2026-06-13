package com.yy.allgomath.fractal.image;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 프랙탈 RGBA 픽셀을 WebP 바이트로 인코딩한다.
 * (기존 FractalController.createImage + convertToWebp 로직을 이전)
 */
@Component
public class FractalImageEncoder {

    /**
     * @param rgbaPixels RGBA 4바이트/픽셀 배열 (FractalResult.getPixels())
     * @param width      이미지 너비
     * @param height     이미지 높이
     * @return WebP 인코딩된 바이트 배열
     */
    public byte[] encodeToWebp(byte[] rgbaPixels, int width, int height) throws IOException {
        BufferedImage image = toBufferedImage(rgbaPixels, width, height);
        ImmutableImage immutableImage = ImmutableImage.wrapAwt(image);
        return immutableImage.bytes(WebpWriter.DEFAULT.withQ(80));
    }

    private BufferedImage toBufferedImage(byte[] pixels, int width, int height) {
        // TYPE_INT_RGB + setRGB 일괄 설정 (기존 동작 보존)
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] rgbArray = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int r = pixels[i * 4] & 0xFF;
            int g = pixels[i * 4 + 1] & 0xFF;
            int b = pixels[i * 4 + 2] & 0xFF;
            rgbArray[i] = (r << 16) | (g << 8) | b;
        }
        image.setRGB(0, 0, width, height, rgbArray, 0, width);
        return image;
    }
}
