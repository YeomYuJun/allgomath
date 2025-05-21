package com.yy.allgomath.datatype;

public class FractalResult {
    private int width;
    private int height;
    private int[][] iterationCounts; // 또는 byte[] 이미지 데이터? 흠..
    private String colorScheme;
    private boolean smooth;
    private byte[] pixels;

    public FractalResult(int width, int height, int[][] iterationCounts) {
        this.width = width;
        this.height = height;
        this.iterationCounts = iterationCounts;
        this.colorScheme = "classic";
        this.smooth = true;
        this.pixels = generatePixels();
    }

    public FractalResult(int width, int height, int[][] iterationCounts, String colorScheme, boolean smooth) {
        this.width = width;
        this.height = height;
        this.iterationCounts = iterationCounts;
        this.colorScheme = colorScheme;
        this.smooth = smooth;
        this.pixels = generatePixels();
    }

    private byte[] generatePixels() {
        byte[] pixels = new byte[width * height * 4]; // RGBA
        int maxIterations = 0;
        
        // 최대 반복 횟수 찾기
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maxIterations = Math.max(maxIterations, iterationCounts[y][x]);
            }
        }

        // 픽셀 데이터 생성
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = (y * width + x) * 4;
                int iterations = iterationCounts[y][x];
                
                if (iterations == maxIterations) {
                    // 프랙탈 내부는 검은색
                    pixels[idx] = 0;     // R
                    pixels[idx + 1] = 0; // G
                    pixels[idx + 2] = 0; // B
                    pixels[idx + 3] = (byte) 255; // A
                } else {
                    // 색상 스키마에 따른 색상 계산
                    double normalized;
                    if (smooth && iterations > 0) {
                        // 부드러운 음영처리를 위한 로그 계산 시 유효성 검사 추가
                        // 음수와 0을 방지하기 위해 값 검증
                        double magnitude = Math.max(1.0e-10, iterationCounts[y][x]);
                        normalized = (iterations + 1 - Math.log(Math.log(magnitude)) / Math.log(2)) / maxIterations;
                    } else {
                        normalized = (double) iterations / maxIterations;
                    }
                    
                    // 정규화된 값을 0-1 범위로 제한
                    normalized = Math.max(0.0, Math.min(1.0, normalized));
                    
                    int[] color = getColor(normalized);
                    // 바이트 변환 시 오버플로우 방지를 위해 & 0xFF 연산 추가
                    pixels[idx] = (byte) (color[0] & 0xFF);     // R
                    pixels[idx + 1] = (byte) (color[1] & 0xFF); // G
                    pixels[idx + 2] = (byte) (color[2] & 0xFF); // B
                    pixels[idx + 3] = (byte) 255;               // A
                }
            }
        }
        
        return pixels;
    }

    private int[] getColor(double normalized) {
        switch (colorScheme.toLowerCase()) {
            case "rainbow":
                return getRainbowColor(normalized);
            case "fire":
                return getFireColor(normalized);
            case "ocean":
                return getOceanColor(normalized);
            case "grayscale":
                return getGrayscaleColor(normalized);
            default: // classic
                return getClassicColor(normalized);
        }
    }

    private int[] getClassicColor(double normalized) {
        int value = (int) (normalized * 255);
        return new int[]{value, value, value};
    }

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

    private int[] getFireColor(double normalized) {
        int r = (int) (Math.min(1.0, normalized * 2) * 255);
        int g = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.5) * 2)) * 255);
        int b = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.75) * 4)) * 255);
        return new int[]{r, g, b};
    }

    private int[] getOceanColor(double normalized) {
        int r = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.5) * 2)) * 255);
        int g = (int) (Math.max(0.0, Math.min(1.0, (normalized - 0.25) * 2)) * 255);
        int b = (int) (Math.min(1.0, normalized * 2) * 255);
        return new int[]{r, g, b};
    }

    private int[] getGrayscaleColor(double normalized) {
        int value = (int) (normalized * 255);
        return new int[]{value, value, value};
    }

    // Getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getIterationCounts() {
        return iterationCounts;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public boolean isSmooth() {
        return smooth;
    }

    public byte[] getPixels() {
        return pixels;
    }
}