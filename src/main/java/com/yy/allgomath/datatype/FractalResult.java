package com.yy.allgomath.datatype;

/**
 * 프랙탈 계산 결과를 저장하는 클래스
 * 픽셀 데이터와 메타데이터를 포함
 */
public class FractalResult {
    private int width;                // 이미지 너비
    private int height;               // 이미지 높이
    private int[][] iterationCounts;  // 각 픽셀의 반복 횟수 또는 히트 카운트
    private String colorScheme;       // 색상 스키마
    private boolean smooth;           // 부드러운 음영 적용 여부
    private byte[] pixels;            // RGBA 형식의 픽셀 데이터

    /**
     * 기본 생성자
     * 클래식 색상 스키마와 부드러운 음영을 기본값으로 사용
     */
    public FractalResult(int width, int height, int[][] iterationCounts) {
        this.width = width;
        this.height = height;
        this.iterationCounts = iterationCounts;
        this.colorScheme = "classic";
        this.smooth = true;
        this.pixels = generatePixels();
    }

    /**
     * 사용자 정의 색상 스키마와 음영 설정을 포함한 생성자
     */
    public FractalResult(int width, int height, int[][] iterationCounts, String colorScheme, boolean smooth) {
        this.width = width;
        this.height = height;
        this.iterationCounts = iterationCounts;
        this.colorScheme = colorScheme;
        this.smooth = smooth;
        this.pixels = generatePixels();
    }

    /**
     * 반복 횟수 또는 히트 카운트를 RGBA 픽셀 데이터로 변환
     * @return RGBA 형식의 바이트 배열
     */
    private byte[] generatePixels() {
        byte[] pixels = new byte[width * height * 4]; // RGBA
        int maxValue = 0;
        
        // 최대값 계산
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maxValue = Math.max(maxValue, iterationCounts[y][x]);
            }
        }

        // 픽셀 데이터 생성
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = (y * width + x) * 4;
                int value = iterationCounts[y][x];
                
                if (value == 0) {
                    // 배경색 설정 (흰색)
                    pixels[idx] = (byte) 255;     // R
                    pixels[idx + 1] = (byte) 255; // G
                    pixels[idx + 2] = (byte) 255; // B
                    pixels[idx + 3] = (byte) 255; // A
                } else {
                    // 색상 스키마에 따른 색상 계산
                    double normalized = (double) value / maxValue;
                    normalized = Math.max(0.0, Math.min(1.0, normalized));
                    
                    int[] color = getColor(normalized);
                    pixels[idx] = (byte) (color[0] & 0xFF);     // R
                    pixels[idx + 1] = (byte) (color[1] & 0xFF); // G
                    pixels[idx + 2] = (byte) (color[2] & 0xFF); // B
                    pixels[idx + 3] = (byte) 255;               // A
                }
            }
        }
        
        return pixels;
    }

    /**
     * 색상 스키마에 따른 색상 계산
     * @param normalized 정규화된 값 (0.0 ~ 1.0)
     * @return RGB 색상 배열
     */
    private int[] getColor(double normalized) {
        switch (colorScheme.toLowerCase()) {
            case "rainbow": return getRainbowColor(normalized);
            case "fire": return getFireColor(normalized);
            case "ocean": return getOceanColor(normalized);
            case "grayscale": return getGrayscaleColor(normalized);
            default: return getClassicColor(normalized);
        }
    }

    /**
     * 클래식 색상 스키마 (흑백)
     */
    private int[] getClassicColor(double normalized) {
        int value = (int) (normalized * 255);
        return new int[]{value, value, value};
    }

    /**
     * 무지개 색상 스키마 (HSV 색상 공간 기반)
     */
    private int[] getRainbowColor(double normalized) {
        double hue = normalized * 6.0;
        double saturation = 1.0;
        double brightness = 1.0;
        
        int h = (int) hue;
        double f = hue - h;
        double p = brightness * (1 - saturation);
        double q = brightness * (1 - saturation * f);
        double t = brightness * (1 - saturation * (1 - f));
        
        int r, g, b;
        switch (h) {
            case 0: r = (int)(brightness * 255); g = (int)(t * 255); b = (int)(p * 255); break;
            case 1: r = (int)(q * 255); g = (int)(brightness * 255); b = (int)(p * 255); break;
            case 2: r = (int)(p * 255); g = (int)(brightness * 255); b = (int)(t * 255); break;
            case 3: r = (int)(p * 255); g = (int)(q * 255); b = (int)(brightness * 255); break;
            case 4: r = (int)(t * 255); g = (int)(p * 255); b = (int)(brightness * 255); break;
            default: r = (int)(brightness * 255); g = (int)(p * 255); b = (int)(q * 255);
        }
        
        return new int[]{r, g, b};
    }

    /**
     * 화염 색상 스키마 (빨강-노랑-파랑 그라데이션)
     */
    private int[] getFireColor(double normalized) {
        int r = (int) (Math.min(1.0, normalized * 2) * 255);
        int g = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.5) * 2)) * 255);
        int b = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.75) * 4)) * 255);
        return new int[]{r, g, b};
    }

    /**
     * 해양 색상 스키마 (파랑-초록-빨강 그라데이션)
     */
    private int[] getOceanColor(double normalized) {
        int r = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.5) * 2)) * 255);
        int g = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.25) * 2)) * 255);
        int b = (int) (Math.min(1.0, normalized * 2) * 255);
        return new int[]{r, g, b};
    }

    /**
     * 흑백 색상 스키마
     */
    private int[] getGrayscaleColor(double normalized) {
        int value = (int) (normalized * 255);
        return new int[]{value, value, value};
    }

    // Getter 메서드들
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int[][] getIterationCounts() { return iterationCounts; }
    public String getColorScheme() { return colorScheme; }
    public boolean isSmooth() { return smooth; }
    public byte[] getPixels() { return pixels; }
}