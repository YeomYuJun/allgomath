package com.yy.allgomath.greedy;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.greedy.dto.GreedyParams;
import com.yy.allgomath.greedy.dto.GreedyResult;
import com.yy.allgomath.greedy.dto.TaskInterval;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GreedyServiceTest {

    private final GreedyService svc = new GreedyService();

    private static List<TaskInterval> tasks3() {
        return List.of(new TaskInterval(0, 10), new TaskInterval(1, 2), new TaskInterval(3, 4));
    }

    // --- finish strategy ---

    @Test
    void finish_strategy_selects_two_tasks() {
        GreedyResult r = svc.compute(new GreedyParams(tasks3(), "finish"));
        assertEquals(2, r.selected());
        assertEquals(2, r.optimal());
    }

    @Test
    void finish_strategy_order_is_by_end_time() {
        // (1,2) ends at 2, (3,4) ends at 4, (0,10) ends at 10 -> order [1,2,0]
        GreedyResult r = svc.compute(new GreedyParams(tasks3(), "finish"));
        assertArrayEquals(new int[]{1, 2, 0}, r.order());
    }

    // --- start strategy ---

    @Test
    void start_strategy_selects_one_task() {
        // sorted by s: (0,10) s=0, (1,2) s=1, (3,4) s=3 -> order [0,1,2]
        // idx0 accepted (lastEnd->10), idx1 rejected (1<10), idx2 rejected (3<10)
        GreedyResult r = svc.compute(new GreedyParams(tasks3(), "start"));
        assertEquals(1, r.selected());
        assertEquals(2, r.optimal());
    }

    @Test
    void start_strategy_order_is_zero_one_two() {
        GreedyResult r = svc.compute(new GreedyParams(tasks3(), "start"));
        assertArrayEquals(new int[]{0, 1, 2}, r.order());
    }

    @Test
    void start_strategy_decisions_length_is_three() {
        GreedyResult r = svc.compute(new GreedyParams(tasks3(), "start"));
        assertEquals(3, r.decisions().length);
    }

    @Test
    void start_strategy_first_decision_is_accepted() {
        GreedyResult r = svc.compute(new GreedyParams(tasks3(), "start"));
        assertTrue(r.decisions()[0].accepted());
    }

    // --- validation ---

    @Test
    void empty_tasks_throws() {
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new GreedyParams(List.of(), "finish")));
    }

    @Test
    void null_tasks_throws() {
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new GreedyParams(null, "finish")));
    }

    @Test
    void task_with_equal_start_end_throws() {
        // s >= e is invalid
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new GreedyParams(List.of(new TaskInterval(5, 5)), "finish")));
    }

    @Test
    void invalid_strategy_throws() {
        assertThrows(InvalidParameterException.class,
                () -> svc.compute(new GreedyParams(tasks3(), "x")));
    }
}
