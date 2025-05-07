package com.yy.allgomath.datatype;

public class GradientStep {
    private int step;      // 단계 번호
    private double x;
    private double y;
    private double z;      // f(x,y) 값 (비용 함수 값)
    private double gradientX; // x에 대한 그래디언트 (선택적 정보)
    private double gradientY; // y에 대한 그래디언트 (선택적 정보)

    public GradientStep(int step, double x, double y, double z, double gradientX, double gradientY) {
        this.step = step;
        this.x = x;
        this.y = y;
        this.z = z;
        this.gradientX = gradientX;
        this.gradientY = gradientY;
    }

    // Getter 메서드들
    public int getStep() { return step; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getGradientX() { return gradientX; }
    public double getGradientY() { return gradientY; }

    // Setter는 필요에 따라 추가 (보통 DTO는 불변으로 만들거나 생성자로만 초기화)
}