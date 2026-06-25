package com.yy.allgomath.dfs;

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
class DfsCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String OPEN_4x4 =
        "\"walls\":[false,false,false,false,false,false,false,false,"
        + "false,false,false,false,false,false,false,false]";

    @Test
    void search_returns_found_path_and_events() throws Exception {
        String body = "{\"rows\":4,\"cols\":4," + OPEN_4x4 + ",\"start\":0,\"goal\":15}";
        mockMvc.perform(post("/api/algorithms/dfs/search")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events[0].type").value("push"))
                .andExpect(jsonPath("$.path[0]").value(0));
    }

    @Test
    void rows_below_minimum_returns_bad_request() throws Exception {
        String body = "{\"rows\":2,\"cols\":4,\"walls\":[false,false,false,false,false,false,false,false],"
                + "\"start\":0,\"goal\":7}";
        mockMvc.perform(post("/api/algorithms/dfs/search")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
