package com.yy.allgomath.bezier;

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
class BezierCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void compute_returns_curve_and_degree() throws Exception {
        String body = "{\"controlPoints\":[[0.1,0.8],[0.3,0.2],[0.7,0.9],[0.9,0.3]],\"samples\":40}";
        mockMvc.perform(post("/api/algorithms/bezier/compute")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.curve.length()").value(41))
                .andExpect(jsonPath("$.degree").value(3));
    }

    @Test
    void too_few_control_points_returns_bad_request() throws Exception {
        String body = "{\"controlPoints\":[[0.1,0.8]],\"samples\":40}";
        mockMvc.perform(post("/api/algorithms/bezier/compute")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void samples_out_of_range_returns_bad_request() throws Exception {
        String body = "{\"controlPoints\":[[0.1,0.8],[0.9,0.3]],\"samples\":1}";
        mockMvc.perform(post("/api/algorithms/bezier/compute")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
