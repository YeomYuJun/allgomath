package com.yy.allgomath.flow;

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
class FlowCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulate_returns_steps_and_series() throws Exception {
        String body = "{\"particles\":[[50,50],[20,80]],\"scale\":1.4,\"time\":0.0,\"steps\":4}";
        mockMvc.perform(post("/api/algorithms/flow/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps.length()").value(4))
                .andExpect(jsonPath("$.series.length()").value(4))
                .andExpect(jsonPath("$.steps[0][0][0]").exists());
    }

    @Test
    void invalid_steps_returns_bad_request() throws Exception {
        String body = "{\"particles\":[[50,50]],\"scale\":1.4,\"time\":0.0,\"steps\":0}";
        mockMvc.perform(post("/api/algorithms/flow/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void empty_particles_returns_bad_request() throws Exception {
        String body = "{\"particles\":[],\"scale\":1.4,\"time\":0.0,\"steps\":4}";
        mockMvc.perform(post("/api/algorithms/flow/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
