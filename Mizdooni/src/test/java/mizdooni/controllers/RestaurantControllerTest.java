package mizdooni.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.model.Restaurant;
import mizdooni.model.RestaurantSearchFilter;
import mizdooni.response.PagedList;
import mizdooni.service.UserService;
import mizdooni.testFixtures.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import mizdooni.service.RestaurantService;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private UserService userService;

    private RestaurantController restaurantController;
    private Restaurant restaurant;

    @BeforeEach
    public void setUp() {
        restaurant = TestFixtures.createSampleRestaurant();
    }

    @Test
    void testGetRestaurant_Success() throws Exception {
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        mockMvc.perform(get("/restaurants/{restaurantId}", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant found"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testGetRestaurant_NotFound() throws Exception {
        mockMvc.perform(get("/restaurants/{restaurantId}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRestaurant_BadRequest() throws Exception {
        mockMvc.perform(get("/restaurants/{restaurantId}", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRestaurants_ValidPage() throws Exception {
        RestaurantSearchFilter filter = new RestaurantSearchFilter();
        ArrayList<Restaurant> restaurant_list = new ArrayList<Restaurant>();
        restaurant_list.add(restaurant);
        int page_number = 1;
        PagedList<Restaurant> pagedRestaurants = new PagedList<Restaurant>(restaurant_list, page_number, 3);
        when(restaurantService.getRestaurants(eq(page_number), any(RestaurantSearchFilter.class))).thenReturn(pagedRestaurants);
        mockMvc.perform(get("/restaurants")
                .param("page", String.valueOf(page_number), "filter", filter.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurants listed"))
                .andExpect(jsonPath("$.data.page").value(1)) // Adjust field names as needed
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.totalPages").value(1)) // Adjust field names as needed
                .andExpect(jsonPath("$.data.pageList").isNotEmpty());
    }

    @Test
    void testGetRestaurants_InvalidPage() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("filter", "first"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetManagerRestaurants_whenExceptionThrown_shouldReturnBadRequest() throws Exception {
        int managerId = 1;
        when(restaurantService.getManagerRestaurants(managerId))
                .thenThrow(new RuntimeException("Some error"));

        mockMvc.perform(get("/restaurants/manager/{managerId}", managerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetManagerRestaurants() throws Exception {
        mockMvc.perform(get("/restaurants/manager/{managerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("manager restaurants listed"))
                .andExpect(jsonPath("$.data").isArray());
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
                .andExpect(jsonPath("$.message").value("parameters missing"));
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
        String name = "Existing Name";
        when(restaurantService.restaurantExists(name)).thenReturn(true);
        mockMvc.perform(get("/validate/restaurant-name")
                        .param("data", "Existing Name"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    void testGetRestaurantTypes() throws Exception {
        when(restaurantService.getRestaurantTypes()).thenReturn(Set.of("a", "b"));
        mockMvc.perform(get("/restaurants/types")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant types"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetRestaurantLocations_Empty() throws Exception {
        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant locations"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetRestaurantLocations_NotEmpty() throws Exception {
        when(restaurantService.getRestaurantLocations()).thenReturn(Map.of("tehran", Set.of("1", "2"), "mash", Set.of("3", "4")));
        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant locations"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }
}