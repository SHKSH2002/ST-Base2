package mizdooni.controllers;

import mizdooni.controllers.TableController;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.response.Response;
import mizdooni.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.service.TableService;
import mizdooni.service.UserService;
import mizdooni.testFixtures.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableController.class)
class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private TableService tableService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant restaurant;

    @BeforeEach
    public void setUp() {
        restaurant = TestFixtures.createSampleRestaurant();
    }

    @Test
    void testGetTables_validRestaurantId_returnsTables() throws Exception {
        when(restaurantService.getRestaurant(1)).thenReturn(restaurant);
        when(tableService.getTables(1)).thenReturn(List.of(new Table(1, 1, 4)));

        mockMvc.perform(get("/tables/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data[0].seatsNumber").value(4));
    }

    @Test
    void getTables_invalidRestaurantId_throwsNotFound() throws Exception {
        when(restaurantService.getRestaurant(anyInt())).thenReturn(null);

        mockMvc.perform(get("/tables/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    void addTable_validRequest_returnsOk() throws Exception {
        when(restaurantService.getRestaurant(anyInt())).thenReturn(restaurant);
        Map<String, Object> requestBody = Map.of(
                    "seatsNumber", "1"
                );

        mockMvc.perform(post("/tables/{restaurantId}", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("table added"));
    }

    @Test
    void addTable_missingSeatsNumber_throwsBadRequest() throws Exception {
        when(restaurantService.getRestaurant(anyInt())).thenReturn(restaurant);

        mockMvc.perform(post("/tables/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.emptyMap())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @Test
    void addTable_invalidSeatsNumberType_throwsBadRequest() throws Exception {
        when(restaurantService.getRestaurant(anyInt())).thenReturn(restaurant);

        mockMvc.perform(post("/tables/{restaurantId}", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonMap("seatNumber", "invalid"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @Test
    void addTable_nonexistentRestaurant_throwsNotFound() throws Exception {
        when(restaurantService.getRestaurant(anyInt())).thenReturn(null);

        mockMvc.perform(post("/tables/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonMap("seatNumber", "4"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }
}
