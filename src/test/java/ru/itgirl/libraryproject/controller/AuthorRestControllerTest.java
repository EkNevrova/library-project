package ru.itgirl.libraryproject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.itgirl.libraryproject.dto.AuthorDto;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAuthorById() throws Exception {
        Long id = 1L;
        AuthorDto authorDto = AuthorDto.builder()
                .id(id)
                .name("Александр")
                .surname("Пушкин")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/author/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(authorDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(authorDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(authorDto.getSurname()));
    }

    @Test
    void testCreateAuthor() throws Exception {
        String json = """
            {
                "name": "John",
                "surname": "Doe"
            }
        """;

        mockMvc.perform(post("/author/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void testGetAuthorByNameV1() throws Exception {
        mockMvc.perform(get("/author/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void testGetAuthorByNameV2() throws Exception {
        mockMvc.perform(get("/author/v2")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void testGetAuthorByNameV3() throws Exception {
        mockMvc.perform(get("/author/v3")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void testUpdateAuthor() throws Exception {
        String json = """
            {
                "id": 1,
                "name": "UpdatedName",
                "surname": "UpdatedSurname"
            }
        """;

        mockMvc.perform(put("/author/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.surname").value("UpdatedSurname"));
    }

    @Test
    void testDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/author/delete/{id}", 13L))
                .andExpect(status().isOk());
    }
}
