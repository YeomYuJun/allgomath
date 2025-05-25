package com.yy.allgomath.datatype;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 2D 점을 나타내는 내부 클래스
 */
@Data
@AllArgsConstructor
public class Point2D {
    private double x;
    private double y;
}