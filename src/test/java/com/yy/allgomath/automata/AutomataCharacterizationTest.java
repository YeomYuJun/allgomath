package com.yy.allgomath.automata;

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
class AutomataCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulate_returns_requested_generations_and_populations() throws Exception {
        // 3x3 blinker(가로 3칸)에서 2세대
        String body = "{\"grid\":[[false,false,false],[true,true,true],[false,false,false]],\"steps\":2}";
        mockMvc.perform(post("/api/algorithms/automata/life/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps.length()").value(2))
                .andExpect(jsonPath("$.series.length()").value(2))
                .andExpect(jsonPath("$.series[0]").value(3.0));
    }

    @Test
    void invalid_steps_returns_bad_request() throws Exception {
        String body = "{\"grid\":[[true]],\"steps\":0}";
        mockMvc.perform(post("/api/algorithms/automata/life/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void empty_grid_returns_bad_request() throws Exception {
        String body = "{\"grid\":[],\"steps\":1}";
        mockMvc.perform(post("/api/algorithms/automata/life/simulate")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
