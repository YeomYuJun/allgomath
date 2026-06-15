package com.yy.allgomath.voronoi;

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
class VoronoiCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void compute_returns_owner_and_edges() throws Exception {
        String body = "{\"sites\":[[0.2,0.5],[0.8,0.5]],\"metric\":\"euclid\",\"grid\":16}";
        mockMvc.perform(post("/api/algorithms/voronoi/compute")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.length()").value(16))
                .andExpect(jsonPath("$.grid").value(16))
                .andExpect(jsonPath("$.edges[0][0]").value(0));
    }

    @Test
    void empty_sites_returns_bad_request() throws Exception {
        String body = "{\"sites\":[],\"metric\":\"euclid\",\"grid\":16}";
        mockMvc.perform(post("/api/algorithms/voronoi/compute")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bad_metric_returns_bad_request() throws Exception {
        String body = "{\"sites\":[[0.2,0.5]],\"metric\":\"chebyshev\",\"grid\":16}";
        mockMvc.perform(post("/api/algorithms/voronoi/compute")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
