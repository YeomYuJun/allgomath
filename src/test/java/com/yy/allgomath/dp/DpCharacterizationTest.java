package com.yy.allgomath.dp;

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
class DpCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void solve_max_2x2_returns_correct_result() throws Exception {
        String body = "{\"grid\":[[1,2],[3,4]],\"mode\":\"max\"}";
        mockMvc.perform(post("/api/algorithms/dp/solve")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.best").value(8))
                .andExpect(jsonPath("$.path.length()").value(3))
                .andExpect(jsonPath("$.from[0][0]").doesNotExist());
    }

    @Test
    void ragged_grid_returns_bad_request() throws Exception {
        String body = "{\"grid\":[[1,2],[3]],\"mode\":\"max\"}";
        mockMvc.perform(post("/api/algorithms/dp/solve")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalid_mode_returns_bad_request() throws Exception {
        String body = "{\"grid\":[[1,2],[3,4]],\"mode\":\"x\"}";
        mockMvc.perform(post("/api/algorithms/dp/solve")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
