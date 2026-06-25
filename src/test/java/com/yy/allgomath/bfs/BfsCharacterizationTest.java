package com.yy.allgomath.bfs;

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
class BfsCharacterizationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String OPEN_4x4 =
        "\"walls\":[false,false,false,false,false,false,false,false,"
        + "false,false,false,false,false,false,false,false]";

    @Test
    void search_returns_path_and_distances() throws Exception {
        String body = "{\"rows\":4,\"cols\":4," + OPEN_4x4 + ",\"start\":0,\"goal\":15,\"diag\":false}";
        mockMvc.perform(post("/api/algorithms/bfs/search")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.dist[15]").value(6))
                .andExpect(jsonPath("$.path.length()").value(7))
                .andExpect(jsonPath("$.order[0]").value(0));
    }

    @Test
    void rows_below_minimum_returns_bad_request() throws Exception {
        String body = "{\"rows\":2,\"cols\":4,\"walls\":[false,false,false,false,false,false,false,false],"
                + "\"start\":0,\"goal\":7,\"diag\":false}";
        mockMvc.perform(post("/api/algorithms/bfs/search")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void start_on_wall_returns_bad_request() throws Exception {
        String walls = "\"walls\":[true,false,false,false,false,false,false,false,"
                + "false,false,false,false,false,false,false,false]";
        String body = "{\"rows\":4,\"cols\":4," + walls + ",\"start\":0,\"goal\":15,\"diag\":false}";
        mockMvc.perform(post("/api/algorithms/bfs/search")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
