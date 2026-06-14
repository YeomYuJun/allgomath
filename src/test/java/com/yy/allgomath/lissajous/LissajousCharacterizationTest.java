package com.yy.allgomath.lissajous;

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
class LissajousCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulate_returns_steps_and_series() throws Exception {
        String body = "{\"a\":3,\"b\":2,\"delta\":1.5708,\"phase\":0.0,\"steps\":5}";
        mockMvc.perform(post("/api/algorithms/lissajous/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps.length()").value(5))
                .andExpect(jsonPath("$.series.length()").value(5))
                .andExpect(jsonPath("$.steps[0].x").exists());
    }

    @Test
    void invalid_steps_returns_bad_request() throws Exception {
        String body = "{\"a\":3,\"b\":2,\"delta\":1.5708,\"phase\":0.0,\"steps\":0}";
        mockMvc.perform(post("/api/algorithms/lissajous/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void a_out_of_range_returns_bad_request() throws Exception {
        String body = "{\"a\":0,\"b\":2,\"delta\":1.5708,\"phase\":0.0,\"steps\":5}";
        mockMvc.perform(post("/api/algorithms/lissajous/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
