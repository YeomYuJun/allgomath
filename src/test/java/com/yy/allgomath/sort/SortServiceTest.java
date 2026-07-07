package com.yy.allgomath.sort;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.sort.dto.SortParams;
import com.yy.allgomath.sort.dto.SortResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortServiceTest {

    private final SortService svc = new SortService();

    @Test
    void all_algorithms_sort_random_input() {
        Random rnd = new Random(42);
        int[] values = rnd.ints(40, -100, 100).toArray();
        int[] expected = values.clone();
        Arrays.sort(expected);
        for (String algo : new String[]{"bubble", "merge", "quick"}) {
            SortResult r = svc.compute(new SortParams(algo, values));
            assertArrayEquals(expected, r.sorted(), algo);
            assertTrue(r.comparisons() > 0, algo);
        }
    }

    @Test
    void replaying_events_reproduces_sorted_array() {
        int[] values = {5, 1, 4, 2, 8, -3, 0, 7};
        for (String algo : new String[]{"bubble", "merge", "quick"}) {
            SortResult r = svc.compute(new SortParams(algo, values));
            int[] replay = values.clone();
            r.events().forEach(e -> {
                switch (e.type()) {
                    case "swap" -> {
                        int tmp = replay[e.a()];
                        replay[e.a()] = replay[e.b()];
                        replay[e.b()] = tmp;
                    }
                    case "write" -> replay[e.a()] = e.b();
                    default -> { }
                }
            });
            assertArrayEquals(r.sorted(), replay, algo);
        }
    }

    @Test
    void bubble_on_sorted_input_exits_early_with_zero_swaps() {
        SortResult r = svc.compute(new SortParams("bubble", new int[]{1, 2, 3, 4, 5}));
        assertEquals(0, r.swaps());
        assertEquals(4, r.comparisons());
    }

    @Test
    void rejects_unknown_algorithm_and_bad_length() {
        assertThrows(InvalidParameterException.class, () -> svc.compute(new SortParams("bogo", new int[]{2, 1})));
        assertThrows(InvalidParameterException.class, () -> svc.compute(new SortParams("bubble", new int[]{1})));
    }
}
