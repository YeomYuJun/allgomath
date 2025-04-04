package com.yy.allgomath.common.constants;

/**
 * 알고리즘 관련 상수 정의
 */
public final class AlgorithmConstants {
    // 정렬 알고리즘 타입
    public static final String SORT_ALGORITHM_QUICK = "quick";
    public static final String SORT_ALGORITHM_MERGE = "merge";
    
    // 함수 타입
    public static final String FUNCTION_TYPE_STANDARD = "standard";
    public static final String FUNCTION_TYPE_MONKEY = "monkey";
    public static final String FUNCTION_TYPE_CUBIC = "cubic";
    public static final String FUNCTION_TYPE_TRIANGLE = "triangle";
    
    // 기본값
    public static final int DEFAULT_ARRAY_SIZE = 10;
    public static final int DEFAULT_MIN_VALUE = 1;
    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_RESOLUTION = 20;
    
    // 에러 메시지
    public static final String ERROR_INVALID_ARRAY_SIZE = "배열 크기는 1 이상이어야 합니다.";
    public static final String ERROR_INVALID_VALUE_RANGE = "최소값은 최대값보다 작거나 같아야 합니다.";
    public static final String ERROR_EMPTY_ARRAY = "배열은 비어있지 않아야 합니다.";
    public static final String ERROR_UNSUPPORTED_ALGORITHM = "지원하지 않는 알고리즘입니다.";
    
    // 생성자 - 인스턴스화 방지
    private AlgorithmConstants() {
        throw new AssertionError("AlgorithmConstants 클래스는 인스턴스화할 수 없습니다.");
    }
}
