package mizdooni.controllers;

import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.*;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReviewControllerTest {
    private Restaurant restaurant;
    private Restaurant restaurant2;
    private User manager;
    private String type;
    private LocalTime startTime;
    private LocalTime endTime;
    private Address address;
    private String imageLink;
    private Table table1;
    private Table table2;
    private User user1;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new User("username", "password", "email", address, User.Role.manager);
        type = "Italian";
        startTime = LocalTime.now();
        endTime = LocalTime.now();
        address = new Address("Iran", "tehran", "AmirAbad");
        imageLink = "image";
        table1 = new Table(1, 1, 4);
        table2 = new Table(2, 1, 3);
        user1 = new User("username1", "password", "email1", address, User.Role.client);
        restaurant = new Restaurant("restaurant1", manager, type, startTime, endTime, "", address, imageLink);
        restaurant.addTable(table1);
        restaurant.addTable(table2);
        restaurant2 = new Restaurant("restaurant2", manager, type, startTime, endTime, "", address, imageLink);
    }

    @Test
    void testGetReviewsReturnsSuccessfulResponse() throws Exception {
        int page = 1;
        PagedList<Review> pagedReviews = new PagedList<Review>(new ArrayList<>(), page, 10);
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reviewService.getReviews(restaurant.getId(), page)).thenReturn(pagedReviews);

        Response response = reviewController.getReviews(restaurant.getId(), page);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertEquals("reviews for restaurant (%d): restaurant1".formatted(restaurant.getId()), response.getMessage());
        assertEquals(pagedReviews, response.getData());
    }

    @Test
    void testGetReviewsForNotExistingRestaurant() {
        int page = 1;

        when(restaurantService.getRestaurant(restaurant.getId())).thenThrow(new ResponseException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.getReviews(restaurant.getId(), page));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetReviewsRaiseExceptionInReviewServiceException() throws RestaurantNotFound {
        int page = 1;

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reviewService.getReviews(restaurant.getId(), page)).thenThrow(new RestaurantNotFound());

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.getReviews(restaurant.getId(), page));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testAddReviewReturnsSuccessfulResponse() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food!");
        Map<String, Number> ratingMap = new HashMap<>();
        ratingMap.put("food", 5);
        ratingMap.put("service", 4);
        ratingMap.put("ambiance", 4);
        ratingMap.put("overall", 5);
        params.put("rating", ratingMap);

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        Response response = reviewController.addReview(restaurant.getId(), params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertEquals("review added successfully", response.getMessage());
        verify(reviewService).addReview(eq(restaurant.getId()), any(Rating.class), eq("Great food!"));
    }

    @Test
    void testAddReviewRaiseForMissingParameters() {
        Map<String, Object> params = new HashMap<>();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.addReview(restaurant.getId(), params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testAddReviewRaiseForInvalidRating() {
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food!");
        Map<String, Number> ratingMap = new HashMap<>();
        ratingMap.put("food", 5);
        params.put("rating", ratingMap);

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.addReview(restaurant.getId(), params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }
}
