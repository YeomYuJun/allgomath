package com.yy.allgomath.pendulum.dto;

/** 한 진자의 상태: 두 각도(라디안)와 각속도. */
public record Bob(double t1, double t2, double w1, double w2) {
}
