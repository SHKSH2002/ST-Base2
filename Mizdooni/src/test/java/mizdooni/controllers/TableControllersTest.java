package mizdooni.controllers;

import mizdooni.controllers.TableController;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.response.Response;
import mizdooni.service.RestaurantService;
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

import java.util.List;

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
    void testGetTables_invalidRestaurantId_throwsException() throws Exception {
        when(restaurantService.restaurantExists("-1")).thenReturn(false);

        mockMvc.perform(get("/tables/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void testAddTable_validInput_addsTable() throws Exception {
        when(restaurantService.restaurantExists("1")).thenReturn(true);

        mockMvc.perform(post("/tables/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatsNumber\": \"4\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("table added"));
    }

    @Test
    void testAddTable_missingSeatsNumber_throwsException() throws Exception {
        when(restaurantService.restaurantExists("1")).thenReturn(true);

        mockMvc.perform(post("/tables/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMS_MISSING"));
    }

    @Test
    void testAddTable_invalidSeatsNumber_throwsException() throws Exception {
        when(restaurantService.restaurantExists("1")).thenReturn(true);

        mockMvc.perform(post("/tables/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatsNumber\": \"four\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMS_BAD_TYPE"));
    }

    @Test
    void testAddTable_nonExistentRestaurant_throwsException() throws Exception {
        when(restaurantService.restaurantExists("99")).thenReturn(false);

        mockMvc.perform(post("/tables/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatsNumber\": \"4\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
