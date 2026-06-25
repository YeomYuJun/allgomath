package com.yy.allgomath.fourier;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class FourierTransformCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void transform_returns_spectrum_and_peaks() throws Exception {
        int N = 64;
        double[] signal = new double[N];
        for (int n = 0; n < N; n++) {
            signal[n] = Math.cos(2 * Math.PI * 8 * n / N);
        }
        String body = objectMapper.writeValueAsString(
                new java.util.HashMap<String, Object>() {{
                    put("signal", signal);
                    put("sampleRate", 64.0);
                }});

        mockMvc.perform(post("/api/algorithms/fourier/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spectrum").isArray())
                .andExpect(jsonPath("$.spectrum.length()").value(33))
                .andExpect(jsonPath("$.peaks").isArray())
                .andExpect(jsonPath("$.peaks.length()").value(1));
    }

    @Test
    void signal_too_short_returns_bad_request() throws Exception {
        double[] signal = new double[4];
        String body = objectMapper.writeValueAsString(
                new java.util.HashMap<String, Object>() {{
                    put("signal", signal);
                    put("sampleRate", 64.0);
                }});

        mockMvc.perform(post("/api/algorithms/fourier/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
