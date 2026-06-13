package com.yy.allgomath.fractal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 리팩토링 회귀 안전망. 현재 /api/fractal/** 의 외부 행위(상태코드, 응답 형태)를 고정한다.
 * 리팩토링 전후 동일하게 통과해야 한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FractalCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void mandelbrot_generate_returnsGridOfRequestedResolution() throws Exception {
        mockMvc.perform(get("/api/fractal/generate")
                        .param("type", "mandelbrot")
                        .param("iterations", "50")
                        .param("resolution", "64")
                        .param("smooth", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.width").value(64))
                .andExpect(jsonPath("$.height").value(64))
                .andExpect(jsonPath("$.smoothValues.length()").value(64))
                .andExpect(jsonPath("$.smoothValues[0].length()").value(64));
    }

    @Test
    void julia_generate_returnsGridOfRequestedResolution() throws Exception {
        mockMvc.perform(get("/api/fractal/generate")
                        .param("type", "julia")
                        .param("iterations", "50")
                        .param("resolution", "64")
                        .param("smooth", "true")
                        .param("juliaReal", "-0.7")
                        .param("juliaImag", "0.27015"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.width").value(64))
                .andExpect(jsonPath("$.height").value(64));
    }

    @Test
    void unsupportedType_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/fractal/generate")
                        .param("type", "no-such-fractal")
                        .param("iterations", "50")
                        .param("resolution", "64"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateImage_returnsNonEmptyWebp() throws Exception {
        byte[] body = mockMvc.perform(get("/api/fractal/generate/image")
                        .param("type", "mandelbrot")
                        .param("iterations", "50")
                        .param("resolution", "64"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/webp"))
                .andReturn().getResponse().getContentAsByteArray();
        org.junit.jupiter.api.Assertions.assertTrue(body.length > 0, "webp body should be non-empty");
    }

    @Test
    void types_containsMandelbrotAndJulia() throws Exception {
        mockMvc.perform(get("/api/fractal/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supportedTypes").value(org.hamcrest.Matchers.hasItems("mandelbrot", "julia")));
    }
}
