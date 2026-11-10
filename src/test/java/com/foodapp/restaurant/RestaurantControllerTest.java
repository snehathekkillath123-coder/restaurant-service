package com.foodapp.restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class RestaurantControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void createRestaurantAddMenuAndQuery() throws Exception {
        mvc.perform(post("/api/restaurants").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Spice Hub\",\"cuisine\":\"Indian\",\"address\":\"Boston\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mvc.perform(post("/api/restaurants/1/menu").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Paneer Tikka\",\"price\":9.50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantId").value(1));

        mvc.perform(get("/api/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paneer Tikka"));
    }

    @Test
    void unknownRestaurantReturns404() throws Exception {
        mvc.perform(get("/api/restaurants/9999")).andExpect(status().isNotFound());
    }

    @Test
    void invalidMenuItemReturns400() throws Exception {
        mvc.perform(post("/api/restaurants").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Cafe\"}")).andExpect(status().isCreated());
        mvc.perform(post("/api/restaurants/1/menu").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
