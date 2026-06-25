package com.yy.allgomath.greedy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GreedyCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void start_strategy_returns_selected_one_optimal_two() throws Exception {
        String body = "{\"tasks\":[{\"s\":0,\"e\":10},{\"s\":1,\"e\":2},{\"s\":3,\"e\":4}],\"strategy\":\"start\"}";
        mockMvc.perform(post("/api/algorithms/greedy/schedule")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected").value(1))
                .andExpect(jsonPath("$.optimal").value(2))
                .andExpect(jsonPath("$.decisions.length()").value(3));
    }

    @Test
    void empty_tasks_returns_bad_request() throws Exception {
        String body = "{\"tasks\":[],\"strategy\":\"finish\"}";
        mockMvc.perform(post("/api/algorithms/greedy/schedule")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
