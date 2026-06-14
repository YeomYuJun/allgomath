package com.yy.allgomath.automata.dto;

/** 입력 그리드의 다음 N세대와 각 세대 인구수. */
public record LifeSimulateResponse(boolean[][][] generations, int[] populations) {
}
