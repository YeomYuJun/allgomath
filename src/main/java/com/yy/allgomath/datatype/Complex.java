package com.yy.allgomath.datatype;

/*
* implementation 'org.apache.commons:commons-math3:3.6.1' 를 사용하여 구현할 수 있으나 일단 사용해봄.
* 복소수 연산을 위한 클래스
*/
public class Complex {
    private final double real;
    private final double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double getReal() {
        return real;
    }

    public double getImag() {
        return imag;
    }

    // 복소수 덧셈: (a+bi) + (c+di) = (a+c) + (b+d)i
    public Complex add(Complex other) {
        return new Complex(this.real + other.real, this.imag + other.imag);
    }

    // 복소수 곱셈: (a+bi) * (c+di) = (ac-bd) + (ad+bc)i
    public Complex multiply(Complex other) {
        double newReal = this.real * other.real - this.imag * other.imag;
        double newImag = this.real * other.imag + this.imag * other.real;
        return new Complex(newReal, newImag);
    }
    // 복소수 뺄셈: (a+bi) - (c+di) = (a-c) + (b-d)i
    public Complex subtract(Complex other) {
        return new Complex(this.real - other.real, this.imag - other.imag);
    }

    // 복소수 크기의 제곱
    public double magnitudeSquared() {
        return real * real + imag * imag;
    }

    // 복소수 크기
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    // 복소수 위상
    public double phase() {
        return Math.atan2(imag, real);
    }

    @Override
    public String toString() {
        if (imag >= 0) {
            return real + " + " + imag + "i";
        } else {
            return real + " - " + (-imag) + "i";
        }
    }
}