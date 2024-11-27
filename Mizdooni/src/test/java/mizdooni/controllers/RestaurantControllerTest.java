package mizdooni.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRestaurant_Success() throws Exception {
        mockMvc.perform(get("/restaurants/{restaurantId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant found"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testGetRestaurant_NotFound() throws Exception {
        mockMvc.perform(get("/restaurants/{restaurantId}", 999))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRestaurants_ValidPage() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurants listed"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testGetRestaurants_InvalidPage() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddRestaurant_Success() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "name", "Test Restaurant",
                "type", "Italian",
                "startTime", "09:00",
                "endTime", "22:00",
                "description", "Test description",
                "address", Map.of(
                        "country", "USA",
                        "city", "New York",
                        "street", "123 Test St."
                )
        );

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant added"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void testAddRestaurant_MissingParams() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "name", "Test Restaurant",
                "type", "Italian"
        );

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("PARAMS_MISSING"));
    }

    @Test
    void testValidateRestaurantName_Available() throws Exception {
        mockMvc.perform(get("/validate/restaurant-name")
                        .param("data", "Unique Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant name is available"));
    }

    @Test
    void testValidateRestaurantName_Taken() throws Exception {
        mockMvc.perform(get("/validate/restaurant-name")
                        .param("data", "Existing Name"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    void testGetRestaurantTypes() throws Exception {
        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant types"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetRestaurantLocations() throws Exception {
        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant locations"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testGetManagerRestaurants() throws Exception {
        mockMvc.perform(get("/restaurants/manager/{managerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("manager restaurants listed"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
