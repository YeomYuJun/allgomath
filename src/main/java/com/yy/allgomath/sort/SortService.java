package com.yy.allgomath.sort;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.simulation.Computer;
import com.yy.allgomath.sort.dto.SortEvent;
import com.yy.allgomath.sort.dto.SortParams;
import com.yy.allgomath.sort.dto.SortResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 비교 정렬(bubble/merge/quick) 트레이스. 전체 이벤트 스트림과 비교/교환/쓰기 카운트를 one-shot으로 반환. */
@Service
public class SortService implements Computer<SortParams, SortResult> {

    private static final Set<String> ALGORITHMS = Set.of("bubble", "merge", "quick");
    private static final int MAX_LEN = 128;
    private static final int MAX_ABS = 1_000_000;

    @Override
    public SortResult compute(SortParams params) {
        validate(params);
        int[] a = params.values().clone();
        Trace t = new Trace();
        switch (params.algorithm()) {
            case "bubble" -> bubble(a, t);
            case "merge" -> mergeSort(a, 0, a.length - 1, t);
            case "quick" -> quick(a, 0, a.length - 1, t);
            default -> throw new InvalidParameterException("algorithm은 bubble/merge/quick 중 하나여야 합니다.");
        }
        return new SortResult(t.events, t.comparisons, t.swaps, t.writes, a);
    }

    private void bubble(int[] a, Trace t) {
        int n = a.length;
        for (int end = n - 1; end > 0; end--) {
            boolean swapped = false;
            for (int i = 0; i < end; i++) {
                t.compare(i, i + 1);
                if (a[i] > a[i + 1]) {
                    int tmp = a[i];
                    a[i] = a[i + 1];
                    a[i + 1] = tmp;
                    t.swap(i, i + 1);
                    swapped = true;
                }
            }
            t.lock(end);
            if (!swapped) {
                for (int i = end - 1; i >= 0; i--) t.lock(i);
                return;
            }
        }
        t.lock(0);
    }

    private void mergeSort(int[] a, int lo, int hi, Trace t) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        mergeSort(a, lo, mid, t);
        mergeSort(a, mid + 1, hi, t);
        int[] tmp = new int[hi - lo + 1];
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi) {
            t.compare(i, j);
            tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        }
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= hi) tmp[k++] = a[j++];
        for (k = 0; k < tmp.length; k++) {
            a[lo + k] = tmp[k];
            t.write(lo + k, tmp[k]);
        }
    }

    private void quick(int[] a, int lo, int hi, Trace t) {
        if (lo > hi) return;
        if (lo == hi) {
            t.lock(lo);
            return;
        }
        t.pivot(hi);
        int p = a[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            t.compare(j, hi);
            if (a[j] < p) {
                if (i != j) {
                    int tmp = a[i];
                    a[i] = a[j];
                    a[j] = tmp;
                    t.swap(i, j);
                }
                i++;
            }
        }
        if (i != hi) {
            int tmp = a[i];
            a[i] = a[hi];
            a[hi] = tmp;
            t.swap(i, hi);
        }
        t.lock(i);
        quick(a, lo, i - 1, t);
        quick(a, i + 1, hi, t);
    }

    private void validate(SortParams p) {
        if (p.algorithm() == null || !ALGORITHMS.contains(p.algorithm())) {
            throw new InvalidParameterException("algorithm은 bubble/merge/quick 중 하나여야 합니다.");
        }
        int[] v = p.values();
        if (v == null || v.length < 2 || v.length > MAX_LEN) {
            throw new InvalidParameterException("values 길이는 2~" + MAX_LEN + "이어야 합니다.");
        }
        for (int x : v) {
            if (Math.abs(x) > MAX_ABS) {
                throw new InvalidParameterException("값의 절대값은 " + MAX_ABS + " 이하여야 합니다.");
            }
        }
    }

    /** 이벤트 누적기. 이벤트 기록과 동시에 카운트를 유지한다. */
    private static final class Trace {
        final List<SortEvent> events = new ArrayList<>();
        int comparisons, swaps, writes;

        void compare(int i, int j) {
            comparisons++;
            events.add(new SortEvent("compare", i, j));
        }

        void swap(int i, int j) {
            swaps++;
            events.add(new SortEvent("swap", i, j));
        }

        void write(int i, int v) {
            writes++;
            events.add(new SortEvent("write", i, v));
        }

        void pivot(int i) {
            events.add(new SortEvent("pivot", i, -1));
        }

        void lock(int i) {
            events.add(new SortEvent("lock", i, -1));
        }
    }
}
