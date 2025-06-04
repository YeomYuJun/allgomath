package com.yy.allgomath.datatype;

/**
 * 프랙탈 계산 결과를 저장하는 클래스
 * 픽셀 데이터와 메타데이터를 포함하며 부드러운 음영을 지원
 */
public class FractalResult {
    private int width;                // 이미지 너비
    private int height;               // 이미지 높이
    private double[][] smoothValues;  // 각 픽셀의 부드러운 반복값 (연속값)
    private String colorScheme;       // 색상 스키마
    private boolean smooth;           // 부드러운 음영 적용 여부
    private byte[] pixels;            // RGBA 형식의 픽셀 데이터 (지연 로딩)

    /**
     * 부드러운 값을 사용하는 생성자
     */
    public FractalResult(int width, int height, double[][] smoothValues, String colorScheme, boolean smooth) {
        this.width = width;
        this.height = height;
        this.smoothValues = smoothValues;
        this.colorScheme = colorScheme;
        this.smooth = smooth;
        // pixels는 지연 로딩으로 처리
    }

    /**
     * 하위 호환성을 위한 생성자 (정수 배열 사용)
     */
    public FractalResult(int width, int height, int[][] iterationCounts) {
        this.width = width;
        this.height = height;
        this.colorScheme = "classic";
        this.smooth = false;
        
        // int[][]를 double[][]로 변환
        this.smoothValues = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.smoothValues[y][x] = iterationCounts[y][x];
            }
        }
    }

    /**
     * 하위 호환성을 위한 생성자 (정수 배열 + 설정)
     */
    public FractalResult(int width, int height, int[][] iterationCounts, String colorScheme, boolean smooth) {
        this.width = width;
        this.height = height;
        this.colorScheme = colorScheme;
        this.smooth = smooth;
        
        // int[][]를 double[][]로 변환
        this.smoothValues = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.smoothValues[y][x] = iterationCounts[y][x];
            }
        }
    }

    /**
     * 지연 로딩으로 픽셀 데이터 생성
     * @return RGBA 형식의 바이트 배열
     */
    public byte[] getPixels() {
        if (pixels == null) {
            pixels = generatePixels();
        }
        return pixels;
    }

    /**
     * 부드러운 값들을 RGBA 픽셀 데이터로 변환
     * @return RGBA 형식의 바이트 배열
     */
    private byte[] generatePixels() {
        byte[] pixels = new byte[width * height * 4]; // RGBA
        
        // 최대값과 최소값 계산 (정규화를 위해)
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double value = smoothValues[y][x];
                if (value > 0) { // 발산한 점들만 고려
                    minValue = Math.min(minValue, value);
                    maxValue = Math.max(maxValue, value);
                }
            }
        }

        // 픽셀 데이터 생성
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = (y * width + x) * 4;
                double value = smoothValues[y][x];
                
                if (value <= 0) {
                    // 수렴하는 점들 (집합 내부) - 검은색
                    pixels[idx] = 0;     // R
                    pixels[idx + 1] = 0; // G
                    pixels[idx + 2] = 0; // B
                    pixels[idx + 3] = (byte) 255; // A
                } else {
                    // 발산하는 점들 - 부드러운 색상 적용
                    double normalized = (value - minValue) / (maxValue - minValue);
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
     * 클래식 색상 스키마 (부드러운 그레이스케일)
     */
    private int[] getClassicColor(double normalized) {
        int value = (int) (normalized * 255);
        return new int[]{value, value, value};
    }

    /**
     * 개선된 무지개 색상 스키마 (HSV에서 RGB로 변환)
     */
    private int[] getRainbowColor(double normalized) {
        double hue = normalized * 300.0; // 0-300도 (보라색 제외)
        double saturation = 0.8;
        double brightness = 0.9;
        
        return hsvToRgb(hue, saturation, brightness);
    }

    /**
     * HSV를 RGB로 변환
     */
    private int[] hsvToRgb(double h, double s, double v) {
        double c = v * s;
        double x = c * (1 - Math.abs(((h / 60.0) % 2) - 1));
        double m = v - c;
        
        double r = 0, g = 0, b = 0;
        
        if (h >= 0 && h < 60) {
            r = c; g = x; b = 0;
        } else if (h >= 60 && h < 120) {
            r = x; g = c; b = 0;
        } else if (h >= 120 && h < 180) {
            r = 0; g = c; b = x;
        } else if (h >= 180 && h < 240) {
            r = 0; g = x; b = c;
        } else if (h >= 240 && h < 300) {
            r = x; g = 0; b = c;
        } else if (h >= 300 && h < 360) {
            r = c; g = 0; b = x;
        }
        
        return new int[]{
            (int) ((r + m) * 255),
            (int) ((g + m) * 255),
            (int) ((b + m) * 255)
        };
    }

    /**
     * 부드러운 화염 색상 스키마
     */
    private int[] getFireColor(double normalized) {
        // 부드러운 화염 색상: 검은색 -> 빨간색 -> 노란색 -> 흰색
        if (normalized < 0.33) {
            double t = normalized / 0.33;
            return new int[]{
                (int) (t * 255),
                0,
                0
            };
        } else if (normalized < 0.66) {
            double t = (normalized - 0.33) / 0.33;
            return new int[]{
                255,
                (int) (t * 255),
                0
            };
        } else {
            double t = (normalized - 0.66) / 0.34;
            return new int[]{
                255,
                255,
                (int) (t * 255)
            };
        }
    }

    /**
     * 부드러운 해양 색상 스키마
     */
    private int[] getOceanColor(double normalized) {
        // 부드러운 해양 색상: 검은색 -> 진한 파란색 -> 하늘색 -> 흰색
        if (normalized < 0.5) {
            double t = normalized / 0.5;
            return new int[]{
                0,
                (int) (t * 128),
                (int) (t * 255)
            };
        } else {
            double t = (normalized - 0.5) / 0.5;
            return new int[]{
                (int) (t * 255),
                (int) (128 + t * 127),
                255
            };
        }
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
    public double[][] getSmoothValues() { return smoothValues; }
    public String getColorScheme() { return colorScheme; }
    public boolean isSmooth() { return smooth; }
    
    /**
     * 하위 호환성을 위한 메서드
     */
    @Deprecated
    public int[][] getIterationCounts() {
        int[][] intValues = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                intValues[y][x] = (int) smoothValues[y][x];
            }
        }
        return intValues;
    }
}