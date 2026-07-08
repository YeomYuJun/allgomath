package com.yy.allgomath.problems;

import com.yy.allgomath.common.exception.InvalidParameterException;

import java.util.List;
import java.util.Map;

/** Map 기반 요청(params/input)에서 값을 형 검사와 함께 꺼내는 공용 헬퍼. */
final class Inputs {

    private Inputs() {}

    static int reqInt(Map<String, Object> m, String key, int min, int max) {
        Object v = m.get(key);
        if (!(v instanceof Number n)) {
            throw new InvalidParameterException(key + "는 숫자여야 합니다.");
        }
        int i = n.intValue();
        if (i < min || i > max) {
            throw new InvalidParameterException(key + "는 " + min + "~" + max + " 사이여야 합니다.");
        }
        return i;
    }

    static double reqDouble(Map<String, Object> m, String key, double min, double max) {
        Object v = m.get(key);
        if (!(v instanceof Number n)) {
            throw new InvalidParameterException(key + "는 숫자여야 합니다.");
        }
        double d = n.doubleValue();
        if (d < min || d > max) {
            throw new InvalidParameterException(key + "는 " + min + "~" + max + " 사이여야 합니다.");
        }
        return d;
    }

    static String reqString(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (!(v instanceof String s) || s.isBlank()) {
            throw new InvalidParameterException(key + "는 비어 있지 않은 문자열이어야 합니다.");
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> reqObjectList(Map<String, Object> m, String key, int minSize, int maxSize) {
        Object v = m.get(key);
        if (!(v instanceof List<?> list)) {
            throw new InvalidParameterException(key + "는 배열이어야 합니다.");
        }
        if (list.size() < minSize || list.size() > maxSize) {
            throw new InvalidParameterException(key + "는 " + minSize + "~" + maxSize + "개여야 합니다.");
        }
        for (Object e : list) {
            if (!(e instanceof Map)) {
                throw new InvalidParameterException(key + "의 각 항목은 객체여야 합니다.");
            }
        }
        return (List<Map<String, Object>>) list;
    }

    static int[][] reqIntMatrix(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (!(v instanceof List<?> rows) || rows.isEmpty()) {
            throw new InvalidParameterException(key + "는 2차원 숫자 배열이어야 합니다.");
        }
        int[][] out = new int[rows.size()][];
        for (int r = 0; r < rows.size(); r++) {
            if (!(rows.get(r) instanceof List<?> cols)) {
                throw new InvalidParameterException(key + "는 2차원 숫자 배열이어야 합니다.");
            }
            out[r] = new int[cols.size()];
            for (int c = 0; c < cols.size(); c++) {
                if (!(cols.get(c) instanceof Number n)) {
                    throw new InvalidParameterException(key + "는 2차원 숫자 배열이어야 합니다.");
                }
                out[r][c] = n.intValue();
            }
        }
        return out;
    }
}
