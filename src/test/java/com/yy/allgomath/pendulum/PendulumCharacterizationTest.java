package com.yy.allgomath.pendulum;

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
class PendulumCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String STATE =
            "\"state\":{\"a\":{\"t1\":1.95,\"t2\":2.67,\"w1\":0,\"w2\":0},\"b\":{\"t1\":1.96,\"t2\":2.67,\"w1\":0,\"w2\":0}}";

    @Test
    void simulate_returns_steps_and_series() throws Exception {
        String body = "{" + STATE + ",\"gravity\":1.2,\"armRatio\":1.0,\"damping\":0.0,\"steps\":5}";
        mockMvc.perform(post("/api/algorithms/pendulum/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps.length()").value(5))
                .andExpect(jsonPath("$.series.length()").value(5))
                .andExpect(jsonPath("$.steps[0].a.t1").exists());
    }

    @Test
    void invalid_steps_returns_bad_request() throws Exception {
        String body = "{" + STATE + ",\"gravity\":1.2,\"armRatio\":1.0,\"damping\":0.0,\"steps\":0}";
        mockMvc.perform(post("/api/algorithms/pendulum/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void gravity_out_of_range_returns_bad_request() throws Exception {
        String body = "{" + STATE + ",\"gravity\":5.0,\"armRatio\":1.0,\"damping\":0.0,\"steps\":5}";
        mockMvc.perform(post("/api/algorithms/pendulum/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
