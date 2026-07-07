package com.yy.allgomath.sort.dto;

/**
 * 정렬 트레이스의 단일 이벤트.
 * type별 a/b 의미: compare(a,b=비교 인덱스), swap(a,b=교환 인덱스),
 * write(a=인덱스, b=기록 값), pivot(a=피벗 인덱스), lock(a=확정 인덱스).
 */
public record SortEvent(String type, int a, int b) {
}
