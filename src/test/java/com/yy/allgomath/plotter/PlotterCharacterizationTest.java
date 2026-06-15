package com.yy.allgomath.plotter;

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
class PlotterCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void surface_returns_grid_and_critical() throws Exception {
        String body = "{\"fn\":\"saddle\",\"range\":2.4,\"resolution\":20}";
        mockMvc.perform(post("/api/algorithms/plotter/surface")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.z.length()").value(21))
                .andExpect(jsonPath("$.critical.x").value(0.0));
    }

    @Test
    void gradient_descent_returns_path() throws Exception {
        String body = "{\"fn\":\"bowl\",\"startX\":2.0,\"startY\":2.0,\"learningRate\":0.1,\"maxIterations\":60}";
        mockMvc.perform(post("/api/algorithms/plotter/gradient-descent")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path[0].x").value(2.0))
                .andExpect(jsonPath("$.iterations").isNumber());
    }

    @Test
    void bad_function_returns_bad_request() throws Exception {
        String body = "{\"fn\":\"nope\",\"range\":2.4,\"resolution\":20}";
        mockMvc.perform(post("/api/algorithms/plotter/surface")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void out_of_range_resolution_returns_bad_request() throws Exception {
        String body = "{\"fn\":\"bowl\",\"range\":2.4,\"resolution\":200}";
        mockMvc.perform(post("/api/algorithms/plotter/surface")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
