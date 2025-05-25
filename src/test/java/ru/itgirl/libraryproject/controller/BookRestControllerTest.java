package ru.itgirl.libraryproject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetBookByNameV1() throws Exception {
        mockMvc.perform(get("/book")
                        .param("name", "Война и мир"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Война и мир"));
    }

    @Test
    void testGetBookByNameV2() throws Exception {
        mockMvc.perform(get("/book/v2")
                        .param("name", "Война и мир"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Война и мир"));
    }

    @Test
    void testGetBookByNameV3() throws Exception {
        mockMvc.perform(get("/book/v3")
                        .param("name", "Война и мир"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Война и мир"));
    }

    @Test
    void testCreateBook() throws Exception {
        String json = """
            {
                "name": "Новая книга",
                "genre": "Фэнтези",
                "authorIds": [1, 2]
            }
        """;

        mockMvc.perform(post("/book/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новая книга"))
                .andExpect(jsonPath("$.genre").value("Фэнтези"));
    }

    @Test
    void testUpdateBook() throws Exception {
        String json = """
            {
                "id": 1,
                "name": "Обновлённая книга",
                "genre": "Научная фантастика"
            }
        """;

        mockMvc.perform(put("/book/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновлённая книга"))
                .andExpect(jsonPath("$.genre").value("Научная фантастика"));
    }

    @Test
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/book/delete/{id}", 1L))
                .andExpect(status().isOk());
    }
}
