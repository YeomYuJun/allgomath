package com.yy.allgomath.fourier;

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
class FourierCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void series_returns_harmonics() throws Exception {
        String body = "{\"wave\":\"square\",\"N\":5}";
        mockMvc.perform(post("/api/algorithms/fourier/series")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.harmonics.length()").value(5))
                .andExpect(jsonPath("$.harmonics[0].n").value(1));
    }

    @Test
    void bad_wave_returns_bad_request() throws Exception {
        String body = "{\"wave\":\"noise\",\"N\":5}";
        mockMvc.perform(post("/api/algorithms/fourier/series")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalid_n_returns_bad_request() throws Exception {
        String body = "{\"wave\":\"square\",\"N\":0}";
        mockMvc.perform(post("/api/algorithms/fourier/series")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
